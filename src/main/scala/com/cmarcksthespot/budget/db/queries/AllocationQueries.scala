package com.cmarcksthespot.budget.db.queries

import com.cmarcksthespot.budget.db.model.{Allocation, AllocationType, Allocations}
import slick.driver.MySQLDriver.api._
import slick.jdbc.meta.MTable
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


trait AllocationQueries {
  def createTable(): Future[Unit]

  def getAllocations(allocationType: AllocationType): Future[Seq[Allocation]]

  def getAllocations(exceptNames: Set[String]): Future[Seq[Allocation]]

  def createAllocation(name: String, saved: Int, weight: Int, cap: Int, allocationType: AllocationType): Future[Allocation]

  def updateAllocation(id: Int, name: String, saved: Int, weight: Int, cap: Int, allocationType: AllocationType): Future[Option[Allocation]]
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

  override def getAllocations(exceptNames: Set[String]): Future[Seq[Allocation]] = {
    db.run(allocations.filterNot(_.name inSet exceptNames).result)
  }

  override def getAllocations(allocationType: AllocationType): Future[Seq[Allocation]] = {
    db.run(allocations.filter(_.allocationType === allocationType.id.bind).result)
  }

  private val allocationInsertQuery = allocations returning (allocations.map(_.id)) into ((row, id) => row.copy(id = id))

  override def createAllocation(name: String, saved: Int, weight: Int, cap: Int, allocationType: AllocationType): Future[Allocation] = {
    db.run(allocationInsertQuery += Allocation(0, name, saved, weight, cap, allocationType))
  }

  override def updateAllocation(id: Int, name: String, saved: Int, weight: Int, cap: Int, allocationType: AllocationType): Future[Option[Allocation]] = {
    updateAllocation(id, { a => a.copy(
      name = name,
      saved = saved,
      weight = weight,
      cap = cap,
      allocationType = allocationType
    )
    })
  }

  private def updateAllocation(allocation: Allocation): DBIO[Option[Allocation]] = {
    allocations.filter(_.id === allocation.id.bind).update(allocation).map {
      case 0 => None
      case _ => Some(allocation)
    }
  }

  private def updateAllocation(id: Int, op: Allocation => Allocation): Future[Option[Allocation]] = {
    val query = allocations.filter(_.id === id.bind).result.headOption.flatMap {
      case None => DBIO.successful(None)
      case Some(allocation) => updateAllocation(op(allocation))
    }
    db.run(query)
  }
}
