package com.cmarcksthespot.budget.api

import com.cmarcksthespot.budget.business.{AllocationBusiness, TransactionBusiness}
import com.cmarcksthespot.budget.model._

class DefaultApiImpl(allocationBusiness: AllocationBusiness,
                     transactionBusiness: TransactionBusiness) extends DefaultApi {
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

  /**
    * createBudget
    *
    * create new budget
    *
    * @param body
    */
  override def createBudget(body: BudgetBody): Budget = allocationBusiness.createBudget(body)

  /**
    * createGoal
    *
    * create a new goal
    *
    * @param body
    */
  override def createGoal(body: GoalBody): Goal = allocationBusiness.createGoal(body)

  /**
    * updateGoal
    *
    * update existing goal
    *
    * @param goalId
    * @param body
    */
  override def updateGoal(goalId: Int, body: GoalBody): Goal = allocationBusiness.updateGoal(goalId, body)

  /**
    * updateBudget
    *
    * update existing budget
    *
    * @param budgetId
    * @param body
    */
  override def updateBudget(budgetId: Int, body: BudgetBody): Budget = allocationBusiness.updateBudget(budgetId, body)

  /**
    * getTransactions
    *
    * get all transactions
    *
    * @param page info on requested page.  supplied by PagedTransactions/prev. if not supplied, starts at first page
    * @param allocationFilter
    * @param payeeFilter
    */
  override def getTransactions(page: Option[String], allocationFilter: Option[Int], payeeFilter: Option[String]): PagedTransactions =
    transactionBusiness.getTransactions(page, allocationFilter, payeeFilter)

  /**
    * allocate
    *
    * allocation a transaction to a goal or a budget
    *
    * @param body
    */
  override def allocate(body: Allocate): Transaction =
    transactionBusiness.allocate(body)
}
