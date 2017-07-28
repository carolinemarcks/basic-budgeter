package com.cmarcksthespot.budget.business

import com.cmarcksthespot.budget.db
import com.cmarcksthespot.budget.db.queries.TransactionQueries
import com.cmarcksthespot.budget.model.{PagedTransactions, Transaction}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

trait TransactionBusiness {
  def getTransactions(page: Option[String], allocationFilter: Option[Int], payeeFilter: Option[String]): PagedTransactions
}
object TransactionBusiness {
  def apply(queries: TransactionQueries) = new TransactionBusinessImpl(queries)
}

private[business] class TransactionBusinessImpl(queries: TransactionQueries) extends TransactionBusiness {
  val TRANSACTION_PAGE_SIZE = 20

  override def getTransactions(strPageInfo: Option[String], allocationFilter: Option[Int], payeeFilter: Option[String]): PagedTransactions = {
    val currPageInfo = strPageInfo.map(PageInfo.deserailize)
    val fut = queries.getTransactionPage(currPageInfo.map(PageInfo.unapply).flatten, allocationFilter, payeeFilter, TRANSACTION_PAGE_SIZE)
    val transactions = Await.result(fut, Duration.Inf).toList
    val prevPage = PageInfo.prevPage(transactions.map(_.postedDate.getTime), currPageInfo, TRANSACTION_PAGE_SIZE)
    PagedTransactions(transactions.map(_.publicModel), prevPage.map(_.toString))
  }
  private implicit class TransactionConverter(t: db.model.Transaction) {
    def publicModel: Transaction = Transaction(
      id = t.id,
      postedDate = new java.util.Date(t.postedDate.getTime).toInstant,
      payee = t.payee,
      amount = t.amount,
      note = t.note,
      allocationId = t.allocationId
    )
  }
}