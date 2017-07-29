package com.cmarcksthespot.budget.business

import com.cmarcksthespot.budget.db
import com.cmarcksthespot.budget.db.queries.AccountQueries
import com.cmarcksthespot.budget.model.Account

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

trait AccountBusiness {
  def setup(): Future[Unit]

  def getAccounts(): List[Account]

  def updateBalance(id: Int, newBalance: Int): Future[Unit]
}
object AccountBusiness {
  def apply(queries: AccountQueries) = new AccountBusinessImpl(queries)
}
private[business] class AccountBusinessImpl(queries: AccountQueries) extends AccountBusiness {
  override def setup(): Future[Unit] = queries.createTable()

  override def getAccounts(): List[Account] = {
    Await.result(queries.getAccounts(), Duration.Inf).map(_.toPublicModel).toList
  }

  override def updateBalance(id: Int, newBalance: Int): Future[Unit] = {
    queries.updateBalance(id, newBalance).map { _ => () }
  }

  private implicit class AccountConverter(account: db.model.Account) {
    def toPublicModel = Account(
      id = account.id,
      name = account.name,
      balance = account.balance
    )
  }
}
