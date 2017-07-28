package com.cmarcksthespot.budget.db

import com.cmarcksthespot.budget.db.queries._
import slick.driver.MySQLDriver.api.Database

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Setup {
  def run(): Future[Unit]
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

  override def run(): Future[Unit] = {
    for {
      _ <- accountsQueries.setup()
      _ <- allocationQueries.setup()
      _ <- transactionQueries.setup()
    } yield ()
  }
}
