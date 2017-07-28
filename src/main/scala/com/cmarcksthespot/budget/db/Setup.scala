package com.cmarcksthespot.budget.db

import com.cmarcksthespot.budget.db.queries._
import slick.driver.MySQLDriver.api.Database

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Setup {
  def createTables(): Future[Unit]
  def mockData(): Future[Unit]
}

object Setup {
  private class Impl(db: Database) extends SetupImpl with SetupDep {
    override val accountsQueries = AccountQueries(db)
    override val transactionQueries = TransactionQueries(db)
    override val allocationQueries = AllocationQueries(db)
  }
  def apply(db: Database): Setup = new Impl(db)
}

private[db] trait SetupDep {
  val accountsQueries: AccountQueries
  val transactionQueries: TransactionQueries
  val allocationQueries: AllocationQueries
}

private[db] trait SetupImpl extends Setup {
  self: SetupDep =>

  override def createTables(): Future[Unit] = {
    for {
      _ <- accountsQueries.createTable()
      _ <- allocationQueries.createTable()
      _ <- transactionQueries.createTable()
    } yield ()
  }

  override def mockData(): Future[Unit] = {
    for {
      _ <- allocationQueries.createAllocation("budget", 1, 1, 1, model.Budget)
      _ <- allocationQueries.createAllocation("goal", 1, 1, 1, model.Goal)
    } yield ()
  }
}
