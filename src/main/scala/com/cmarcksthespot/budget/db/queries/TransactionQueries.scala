package com.cmarcksthespot.budget.db.queries

import com.cmarcksthespot.budget.db.model.Transactions
import slick.driver.MySQLDriver.api._
import slick.jdbc.meta.MTable
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait TransactionQueries {
  def createTable(): Future[Unit]
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
}
