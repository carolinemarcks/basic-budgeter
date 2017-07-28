package com.cmarcksthespot.budget.db

import com.cmarcksthespot.budget.db.queries.AccountQueries
import slick.driver.MySQLDriver.api.Database

import scala.concurrent.Future

trait Setup {
  def createTables(): Future[Unit]
}

object Setup {
  private class Impl(db: Database) extends SetupImpl with SetupDep {
    val accountsQueries = AccountQueries(db)
  }
  def apply(db: Database): Setup = new Impl(db)
}

private[db] trait SetupDep {
  val accountsQueries: AccountQueries
}

private[db] trait SetupImpl extends Setup {
  self: SetupDep =>

  override def createTables(): Future[Unit] = {
    accountsQueries.createTable()
  }
}
