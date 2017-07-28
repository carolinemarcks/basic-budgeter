package com.cmarcksthespot.budget.api

import com.cmarcksthespot.budget.business.AllocationBusiness
import com.cmarcksthespot.budget.model.{Budget, Goal, Status}

class DefaultApiImpl(allocationBusiness: AllocationBusiness) extends DefaultApi {
  /**
    * ping
    *
    * ping server to check if it&#39;s up
    *
    */
  override def ping(): Status = Status("service is up")

  /**
    * getBudgets
    *
    * get a list of all budgets
    *
    */
  override def getBudgets(): List[Budget] = allocationBusiness.getBudgets()

  /**
    * getGoals
    *
    * get a list of all goals
    *
    */
  override def getGoals(): List[Goal] = allocationBusiness.getGoals()
}
