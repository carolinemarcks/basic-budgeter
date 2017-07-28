package com.cmarcksthespot.budget.business

import com.cmarcksthespot.budget.db
import com.cmarcksthespot.budget.db.queries.AllocationQueries
import com.cmarcksthespot.budget.model.{Budget, Goal}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

trait AllocationBusiness {
  def getBudgets(): List[Budget]

  def getGoals(): List[Goal]
}

object AllocationBusiness {
  def apply(queries: AllocationQueries) = new AllocationBusinessImpl(queries)
}

private[business] class AllocationBusinessImpl(queries: AllocationQueries) extends AllocationBusiness {
  override def getBudgets(): List[Budget] = {
    Await.result(queries.getAllocations(db.model.Budget), Duration.Inf).flatMap(_.toBudget).toList
  }

  override def getGoals(): List[Goal] = {
    Await.result(queries.getAllocations(db.model.Goal), Duration.Inf).flatMap(_.toGoal).toList
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
