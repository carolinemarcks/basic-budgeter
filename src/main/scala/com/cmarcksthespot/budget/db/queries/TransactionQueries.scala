package com.cmarcksthespot.budget.db.queries

import com.cmarcksthespot.budget.db.model.{Transaction, Transactions}
import slick.driver.MySQLDriver.api._
import slick.jdbc.meta.MTable
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait TransactionQueries {
  def createTable(): Future[Unit]

  def getTransactionPage(pageInfo: Option[(Long, Int)], allocationFilter: Option[Int], payeeFilter: Option[String], pageSize: Int): Future[Seq[Transaction]]
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

}
