package com.cmarcksthespot.budget.db.queries

import com.cmarcksthespot.budget.db.model.Goals
import slick.driver.MySQLDriver.api._
import slick.jdbc.meta.MTable
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait GoalQueries {
  def createTable(): Future[Unit]
}
object GoalQueries {
  def apply(db: Database) = new GoalQueriesImpl(db)
}

private[db] class GoalQueriesImpl(db: Database) extends GoalQueries {
  private val goals: TableQuery[Goals] = TableQuery[Goals]

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
