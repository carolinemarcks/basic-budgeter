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

  def distributeIncome(): Unit

  def getHistory(): List[Month]

  def setup(): Future[Unit]

  //TODO business class should not talk in terms of db models
  def insertTransactions(transactions: Iterable[db.model.Transaction]): Future[Unit]
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
      _ <- updateGoals(updatedGoals.values)
      _ <- updateBudgets(updatedBudgets.values)
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

  private def updateGoals(goals: Iterable[Goal]): Future[Unit] = {
    Future.sequence(goals.map { goal =>
      allocationBusiness.updateGoal(goal.id, goal)
    }).map {_ => ()}
  }

  private def updateBudgets(budgets: Iterable[Budget]): Future[Unit] = {
    Future.sequence(budgets.map { budget =>
      allocationBusiness.updateBudget(budget.id, budget)
    }).map {_ => ()}
  }

  override def balance(): Unit = {
    val fut = for {
      transactions <- queries.getUnbalanced()
      (goals, budgets) <- allocationBusiness.getBalanceable()
      _ <- balance(transactions, goals, budgets)
    } yield ()
    Await.result(fut, Duration.Inf)
  }

  private def distributeIncome(goals: List[Goal], budgets: List[Budget]): Future[Unit] = {
    val income = budgets.find(allocationBusiness.isIncome).get

    val (budgetedAmt, newBudgets) = budgets.map { budget =>
      val toAdd = Math.min(budget.amount, budget.cap.getOrElse(Int.MaxValue) - budget.saved)
      (toAdd, budget.copy(saved = budget.saved + toAdd))
    }.unzip

    val newGoals = goals.foldLeft[(Int, Int, List[Goal])]((income.saved - budgetedAmt.sum, goals.map(_.weight).sum, Nil)) {
      case ((amtLeft, stonesLeft, updatedGoals), goal) =>
        val toAdd = Math.min(amtLeft * goal.weight / stonesLeft, goal.cap.getOrElse(Int.MaxValue) - goal.saved)
        (amtLeft - toAdd, stonesLeft - goal.weight, goal.copy(saved = goal.saved + toAdd):: updatedGoals)
    }._3

    for {
      _ <- updateBudgets(newBudgets)
      _ <- updateGoals(newGoals)
    } yield ()
  }

  private def bailIfUnbalanced(unbalanced: List[db.model.Transaction]): Future[Unit] = {
    if (unbalanced.nonEmpty) {
      throw new RuntimeException("cannot distribute income if there are unbalanced transactions")
    } else Future.successful(())
  }

  override def distributeIncome(): Unit = {
    val fut = for {
      unbalanced <- queries.getUnbalanced()
      _ <- bailIfUnbalanced(unbalanced)
      (goals, budgets) <- allocationBusiness.getAll()
      _ <- distributeIncome(goals, budgets)
    } yield ()
    Await.result(fut, Duration.Inf)
  }

  override def getHistory(): List[Month] = {
    val accounts = accountBusiness.getAccounts()
    val netWorth = accounts.map(_.balance).sum

    //TODO don't hardcode month year, auto calculate it based on curdate
    val fut = for {
      incomeId <- allocationBusiness.getIncomeId()
      all <- queries.transactionsByMonthYear(0, 2017, None)
      incomeOnly <- queries.transactionsByMonthYear(0, 2017, Some(incomeId))
    } yield {
      val incomeByMonthYear: Map[(Int, Int), Int] = incomeOnly.toMap
      all
        .sortBy { case ((month, year), _) => year * 12 + month }
        .reverse
        .foldLeft[(Int, List[Month])]((netWorth, Nil)) {
        case ((newNetWorth, accum), ((month, year), transactionSum)) =>
          val monthlyIncome = incomeByMonthYear.get((month, year)).getOrElse(0)
          val monthSpending = monthlyIncome - transactionSum
          val thisMonth = Month(
            month = month,
            year = year,
            net = newNetWorth,
            spent = monthSpending,
            earned = monthlyIncome
          )
          (newNetWorth - transactionSum, thisMonth::accum)
      }._2
    }
    Await.result(fut, Duration.Inf)
  }

  //TODO business class should not talk in terms of db models
  override def insertTransactions(transactions: Iterable[db.model.Transaction]): Future[Unit] = {
    queries.bulkInsertTransactions(transactions).map { _ => }
  }
}