package com.cmarcksthespot.budget.db.queries

import com.cmarcksthespot.budget.db.model.{Account, Accounts}
import slick.driver.MySQLDriver.api._
import slick.jdbc.meta.MTable
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait AccountQueries {
  def createTable(): Future[Unit]

  def getAccounts(): Future[Seq[Account]]
}
object AccountQueries {
  def apply(db: Database) = new AccountQueriesImpl(db)
}

private[db] class AccountQueriesImpl(db: Database) extends AccountQueries {
  private val accounts: TableQuery[Accounts] = TableQuery[Accounts]

  override def createTable(): Future[Unit] = {
    db.run(MTable.getTables).flatMap { existingTables =>
      val existingTableNames = existingTables.map(t => t.name.name)
      if (!existingTableNames.contains(accounts.baseTableRow.tableName)) {
        println(s"creating ${accounts.baseTableRow.tableName}")
        db.run(accounts.schema.create)
      } else {
        Future.successful(())
      }
    }
  }

  override def getAccounts(): Future[Seq[Account]] =
    db.run(accounts.result)
}
