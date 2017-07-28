package com.cmarcksthespot.budget.db.queries

import com.cmarcksthespot.budget.db.model.Budgets
import slick.driver.MySQLDriver.api._
import slick.jdbc.meta.MTable
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait BudgetQueries {
  def createTable(): Future[Unit]
}
object BudgetQueries {
  def apply(db: Database) = new BudgetQueriesImpl(db)
}

private[db] class BudgetQueriesImpl(db: Database) extends BudgetQueries {
  private val budgets: TableQuery[Budgets] = TableQuery[Budgets]

  override def createTable(): Future[Unit] = {
    db.run(MTable.getTables).flatMap { existingTables =>
      val existingTableNames = existingTables.map(t => t.name.name)
      if (!existingTableNames.contains(budgets.baseTableRow.tableName)) {
        println(s"creating ${budgets.baseTableRow.tableName}")
        db.run(budgets.schema.create)
      } else {
        Future.successful(())
      }
    }
  }
}
