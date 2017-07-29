package com.cmarcksthespot.budget.business

import com.cmarcksthespot.budget.db
import com.cmarcksthespot.budget.db.queries.AccountQueries
import com.cmarcksthespot.budget.model.Account

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

trait AccountBusiness {
  def setup(): Future[Unit]

  def getAccounts(): List[Account]
}
object AccountBusiness {
  def apply(queries: AccountQueries) = new AccountBusinessImpl(queries)
}
private[business] class AccountBusinessImpl(queries: AccountQueries) extends AccountBusiness {
  override def setup(): Future[Unit] = queries.createTable()

  def getAccounts(): List[Account] = {
    Await.result(queries.getAccounts(), Duration.Inf).map(_.toPublicModel).toList
  }

  private implicit class AccountConverter(account: db.model.Account) {
    def toPublicModel = Account(account.name, account.balance)
  }
}
