package com.cmarcksthespot.budget.db.queries

import com.cmarcksthespot.budget.db.model.Categories
import slick.driver.MySQLDriver.api._
import slick.jdbc.meta.MTable
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait CategoryQueries {
  def createTable(): Future[Unit]
}
object CategoryQueries {
  def apply(db: Database) = new CategoryQueriesImpl(db)
}

private[db] class CategoryQueriesImpl(db: Database) extends CategoryQueries {
  private val goals: TableQuery[Categories] = TableQuery[Categories]

  override def createTable(): Future[Unit] = {
    db.run(MTable.getTables).flatMap { existingTables =>
      val existingTableNames = existingTables.map(t => t.name.name)
      if (!existingTableNames.contains(goals.baseTableRow.tableName)) {
        println(s"creating ${goals.baseTableRow.tableName}")
        db.run(goals.schema.create)
      } else {
        Future.successful(())
      }
    }
  }
}
