package com.cmarcksthespot.budget.db.queries

import com.cmarcksthespot.budget.db.model.Allocations
import slick.driver.MySQLDriver.api._
import slick.jdbc.meta.MTable
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait AllocationQueries {
  def createTable(): Future[Unit]
}
object AllocationQueries {
  def apply(db: Database) = new AllocationQueriesImpl(db)
}

private[db] class AllocationQueriesImpl(db: Database) extends AllocationQueries {
  private val allocations: TableQuery[Allocations] = TableQuery[Allocations]

  override def createTable(): Future[Unit] = {
    db.run(MTable.getTables).flatMap { existingTables =>
      val existingTableNames = existingTables.map(t => t.name.name)
      if (!existingTableNames.contains(allocations.baseTableRow.tableName)) {
        println(s"creating ${allocations.baseTableRow.tableName}")
        db.run(allocations.schema.create)
      } else {
        Future.successful(())
      }
    }
  }
}
