package com.cmarcksthespot.budget.business

import com.cmarcksthespot.budget.db
import com.cmarcksthespot.budget.db.queries.AllocationQueries
import com.cmarcksthespot.budget.model.{Budget, BudgetBody, Goal, GoalBody}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

trait AllocationBusiness {
  def createBudget(body: BudgetBody): Budget

  def createGoal(body: GoalBody): Goal

  def getBudgets(): List[Budget]

  def getGoals(): List[Goal]

  def updateBudget(budgetId: Int, body: BudgetBody): Budget

  def updateGoal(goalId: Int, body: GoalBody): Goal

  def setup(): Future[Unit]
}

object AllocationBusiness {

  def apply(queries: AllocationQueries) = new AllocationBusinessImpl(queries)
}

private[business] class AllocationBusinessImpl(queries: AllocationQueries) extends AllocationBusiness {

  private val UNCATEGORIZED = "uncategorized"
  private val INCOME = "income"

  override def setup(): Future[Unit] = {
    for {
      _ <- queries.createTable()
      _ <- queries.createAllocation(UNCATEGORIZED, 0, 0, 0, db.model.Budget)
      _ <- queries.createAllocation(INCOME, 0, 0, 0, db.model.Budget)
    } yield ()
  }

  override def getBudgets(): List[Budget] = {
    Await.result(queries.getAllocations(db.model.Budget), Duration.Inf).flatMap(_.toBudget).toList
  }

  override def getGoals(): List[Goal] = {
    Await.result(queries.getAllocations(db.model.Goal), Duration.Inf).flatMap(_.toGoal).toList
  }

  override def createBudget(body: BudgetBody): Budget = {
    val fut = queries.createAllocation(
      name = body.name,
      saved = body.saved,
      weight = body.amount,
      cap = body.cap.getOrElse(0),
      allocationType = db.model.Budget
    )
    Await.result(fut, Duration.Inf).toBudget.get
  }

  override def createGoal(body: GoalBody): Goal = {
    val fut = queries.createAllocation(
      name = body.name,
      saved = body.saved,
      weight = body.weight,
      cap = body.cap.getOrElse(0),
      allocationType = db.model.Goal
    )
    Await.result(fut, Duration.Inf).toGoal.get
  }

  override def updateGoal(goalId: Int, body: GoalBody): Goal = {
    val fut = queries.updateAllocation(goalId,
      name = body.name,
      saved = body.saved,
      weight = body.weight,
      cap = body.cap.getOrElse(0),
      allocationType = db.model.Goal
    )
    Await.result(fut, Duration.Inf).flatMap(_.toGoal).get
  }

  override def updateBudget(budgetId: Int, body: BudgetBody): Budget = {
    val fut = queries.updateAllocation(budgetId,
      name = body.name,
      saved = body.saved,
      weight = body.amount,
      cap = body.cap.getOrElse(0),
      allocationType = db.model.Budget
    )
    Await.result(fut, Duration.Inf).flatMap(_.toBudget).get
  }

  private implicit class AllocationConverter(allocation: db.model.Allocation) {
    def toBudget: Option[Budget] = {
      if (allocation.allocationType == db.model.Budget) {
        Some(Budget(
          id = allocation.id,
          name = allocation.name,
          saved = allocation.saved,
          amount = allocation.weight,
          cap = Some(allocation.cap).filterNot(_.equals(0))
        ))
      } else None
    }
    def toGoal: Option[Goal] = {
      if (allocation.allocationType == db.model.Goal) {
        Some(Goal(
          id = allocation.id,
          name = allocation.name,
          saved = allocation.saved,
          weight = allocation.weight,
          cap = Some(allocation.cap).filterNot(_.equals(0))
        ))
      } else None
    }
  }

}
