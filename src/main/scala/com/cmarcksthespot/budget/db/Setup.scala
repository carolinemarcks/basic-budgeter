package com.cmarcksthespot.budget.db

import com.cmarcksthespot.budget.db.queries._
import slick.driver.MySQLDriver.api.Database

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Setup {
  def createTables(): Future[Unit]
}

object Setup {
  private class Impl(db: Database) extends SetupImpl with SetupDep {
    override val accountsQueries = AccountQueries(db)
    override val transactionQueries = TransactionQueries(db)
    override val budgetQueries = BudgetQueries(db)
    override val goalQueries = GoalQueries(db)
    override val categoryQueries = CategoryQueries(db)
  }
  def apply(db: Database): Setup = new Impl(db)
}

private[db] trait SetupDep {
  val accountsQueries: AccountQueries
  val transactionQueries: TransactionQueries
  val budgetQueries: BudgetQueries
  val goalQueries: GoalQueries
  val categoryQueries: CategoryQueries
}

private[db] trait SetupImpl extends Setup {
  self: SetupDep =>

  override def createTables(): Future[Unit] = {
    for {
      _ <- accountsQueries.createTable()
      _ <- transactionQueries.createTable()
      _ <- budgetQueries.createTable()
      _ <- goalQueries.createTable()
      _ <- categoryQueries.createTable()
    } yield ()
  }
}
