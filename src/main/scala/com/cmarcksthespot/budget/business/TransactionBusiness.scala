package com.cmarcksthespot.budget.business

import com.cmarcksthespot.budget.db
import com.cmarcksthespot.budget.db.queries.TransactionQueries
import com.cmarcksthespot.budget.model._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

trait TransactionBusiness {
  def getTransactions(page: Option[String], allocationFilter: Option[Int], payeeFilter: Option[String]): PagedTransactions

  def allocate(body: Allocate): Transaction

  def balance(): Unit

  def setup(): Future[Unit]
}

object TransactionBusiness {
  def apply(accountBusiness: AccountBusiness, allocationBusiness: AllocationBusiness, queries: TransactionQueries) =
    new TransactionBusinessImpl(accountBusiness, allocationBusiness, queries)
}

private[business] class TransactionBusinessImpl(accountBusiness: AccountBusiness, allocationBusiness: AllocationBusiness, queries: TransactionQueries) extends TransactionBusiness {

  override def setup(): Future[Unit] = {
    queries.createTable()
  }

  val TRANSACTION_PAGE_SIZE = 20
  override def getTransactions(strPageInfo: Option[String], allocationFilter: Option[Int], payeeFilter: Option[String]): PagedTransactions = {
    val currPageInfo = strPageInfo.map(PageInfo.deserailize)
    val fut = queries.getTransactionPage(currPageInfo.map(PageInfo.unapply).flatten, allocationFilter, payeeFilter, TRANSACTION_PAGE_SIZE)
    val transactions = Await.result(fut, Duration.Inf).toList
    val prevPage = PageInfo.prevPage(transactions.map(_.postedDate.getTime), currPageInfo, TRANSACTION_PAGE_SIZE)
    PagedTransactions(transactions.map(_.publicModel), prevPage.map(_.toString))
  }

  override def allocate(body: Allocate): Transaction = {
    Await.result(queries.allocate(body.transactionId, body.allocationId), Duration.Inf).get.publicModel
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

  type Updates = (List[db.model.Transaction], Map[Int, Goal], Map[Int, Budget])
  private def accumulate(updates: Updates, transaction: db.model.Transaction): Updates = {
    val (adjustedTransactions, adjustedGoals, adjustedBudgets) = updates

    adjustedGoals.get(transaction.allocationId).map { goal =>
      val adjustedGoal = goal.copy(saved = goal.saved + transaction.amount)
      (transaction :: adjustedTransactions, adjustedGoals + (goal.id -> adjustedGoal), adjustedBudgets)
    }.orElse {
      adjustedBudgets.get(transaction.allocationId).map { budget =>
        val adjustedBudget = budget.copy(saved = budget.saved + transaction.amount)
        (transaction :: adjustedTransactions, adjustedGoals, adjustedBudgets + (budget.id -> adjustedBudget))
      }
    }.getOrElse((adjustedTransactions, adjustedGoals, adjustedBudgets))
  }

  private def balance(transactions: List[db.model.Transaction], goals: List[Goal], budgets: List[Budget]): Future[Unit] = {
    val goalIdToGoal = goals.map { g => (g.id, g) }.toMap
    val budgetIdToBudget = budgets.map { b => (b.id, b) }.toMap

    val (updatedTransactions, updatedGoals, updatedBudgets) =
      transactions.foldLeft[Updates]((Nil, goalIdToGoal, budgetIdToBudget)) {
        case (updates, transaction) => accumulate(updates, transaction)
      }

    for {
      _ <- queries.markBalanced(updatedTransactions.map(_.id).toSet)
      _ <- updateGoals(updatedGoals)
      _ <- updateBudgets(updatedBudgets)
      _ <- updateAccounts(updatedTransactions)
    } yield ()
  }

  private def updateAccounts(transactions: List[db.model.Transaction]): Future[Unit] = {
    val accounts = accountBusiness.getAccounts()
    val transactionsByAccount = transactions.groupBy(_.accountId)
    Future.sequence(accounts.map { account =>
      transactionsByAccount.get(account.id).map { transactions =>
        val newBalance = account.balance + transactions.map(_.amount).sum
        accountBusiness.updateBalance(account.id, newBalance)
      }.getOrElse(Future.successful(()))
    }).map { _ => () }
  }

  private def updateGoals(goals: Map[Int, Goal]): Future[Unit] = {
    Future.sequence(goals.map { case (id, goal) =>
      allocationBusiness.updateGoal(id, goal)
    }).map {_ => ()}
  }

  private def updateBudgets(budgets: Map[Int, Budget]): Future[Unit] = {
    Future.sequence(budgets.map { case (id, budget) =>
      allocationBusiness.updateBudget(id, budget)
    }).map {_ => ()}
  }

  override def balance(): Unit = {
    for {
      transactions <- queries.getUnbalanced()
      (goals, budgets) <- allocationBusiness.getBalanceable()
      _ <- balance(transactions, goals, budgets)
    } yield ()
  }
}