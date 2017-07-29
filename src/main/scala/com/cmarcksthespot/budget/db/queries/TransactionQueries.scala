package com.cmarcksthespot.budget.db.queries

import java.sql.Date

import com.cmarcksthespot.budget.db.model.{Transaction, Transactions}
import slick.driver.MySQLDriver.api._
import slick.jdbc.meta.MTable
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait TransactionQueries {
  def createTable(): Future[Unit]

  def getTransactionPage(pageInfo: Option[(Long, Int)], allocationFilter: Option[Int], payeeFilter: Option[String], pageSize: Int): Future[Seq[Transaction]]

  def allocate(id: String, allocationId: Int): Future[Option[Transaction]]

  def getUnbalanced(): Future[List[Transaction]]

  def markBalanced(ids: Set[String]): Future[Int]

  def transactionsByMonthYear(sinceMonth: Int, sinceYear: Int, allocationId: Option[Int]): Future[List[((Int, Int), Int)]]
}

object TransactionQueries {
  def apply(db: Database) = new TransactionQueriesImpl(db)
}

private[db] class TransactionQueriesImpl(db: Database) extends TransactionQueries {
  private val transactions: TableQuery[Transactions] = TableQuery[Transactions]

  override def createTable(): Future[Unit] = {
    db.run(MTable.getTables).flatMap { existingTables =>
      val existingTableNames = existingTables.map(t => t.name.name)
      if (!existingTableNames.contains(transactions.baseTableRow.tableName)) {
        println(s"creating ${transactions.baseTableRow.tableName}")
        db.run(transactions.schema.create)
      } else {
        Future.successful(())
      }
    }
  }

  override def getTransactionPage(pageInfo: Option[(Long, Int)], allocationFilter: Option[Int], payeeFilter: Option[String], pageSize: Int): Future[Seq[Transaction]] =  {
    val queryBase = transactions.sortBy { t => (t.postedDate.desc, t.id) }
    val pagedQuery = pageInfo match {
      case Some((millis, offset)) =>
        val sqlDate = new java.sql.Date(millis)
        queryBase
          .filter(_.postedDate <= sqlDate.bind)
          .drop(offset)
      case None =>
        queryBase
    }
    val allocationFilteredQuery = allocationFilter match {
      case Some(allocationId) => pagedQuery.filter(_.allocationId === allocationId.bind)
      case None => pagedQuery
    }
    val payeeFilteredQuery = payeeFilter match {
      case Some(payeeName) => allocationFilteredQuery.filter(_.payee like s"%$payeeName%") // TODO sanitize input
      case None => allocationFilteredQuery
    }

    db.run(payeeFilteredQuery.take(pageSize).result)
  }

  override def allocate(id: String, allocationId: Int): Future[Option[Transaction]] = {
    val query = transactions.filter(_.id === id.bind).result.headOption.flatMap {
      case None => DBIO.successful(None)
      case Some(transaction) => updateTransaction(transaction.copy(allocationId = allocationId))
    }
    db.run(query)
  }

  private def updateTransaction(transaction: Transaction): DBIO[Option[Transaction]] = {
    transactions.filter(_.id === transaction.id.bind).update(transaction).map {
      case 0 => None
      case _ => Some(transaction)
    }
  }

  override def getUnbalanced(): Future[List[Transaction]] = {
    db.run(transactions.filter(_.isBalanced).result).map(_.toList)
  }

  override def markBalanced(ids: Set[String]): Future[Int] = {
    val query = transactions.filter(_.id inSet ids).map(_.isBalanced).update(true)
    db.run(query)
  }

  private val monthFn = SimpleFunction.unary[Date, Int]("month")
  private val yearFn = SimpleFunction.unary[Date, Int]("year")

  override def transactionsByMonthYear(sinceMonth: Int, sinceYear: Int, allocationId: Option[Int]): Future[List[((Int, Int), Int)]] = {
    val baseQuery = allocationId
      .map { aid => transactions.filter(_.allocationId === aid.bind ) }
      .getOrElse(transactions)

    val fullQuery = baseQuery.filter { t =>
      monthFn(t.postedDate) >= sinceMonth && yearFn(t.postedDate) >= sinceYear
    }.groupBy { t =>
      (monthFn(t.postedDate), yearFn(t.postedDate))
    }.map {
      case (monthYear, transactions) => (monthYear, transactions.map(_.amount).sum)
    }

    db.run(fullQuery.result).map { _.map { case (monthYear, amount) => (monthYear, amount.getOrElse(0)) }.toList }
  }
}
