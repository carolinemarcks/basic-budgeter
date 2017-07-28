package com.cmarcksthespot.budget.db.model

import slick.driver.MySQLDriver.api._
import slick.lifted.ProvenShape

case class Category(id: Int, name: String, description: String, budgetId: Int)

private[db] class Categories(tag: Tag)
      extends Table[Category](tag, "categories") {
  def id: Rep[Int] = column[Int]("id", O.PrimaryKey)

  def name: Rep[String] = column[String]("name", O.Length(50, varying = true))

  def description: Rep[String] = column[String]("description", O.Length(50, varying = true))

  def budgetId: Rep[Int] = column[Int]("budget_id")

  def budget =
    foreignKey("budget_fk", budgetId, TableQuery[Budgets])(_.id)

  def * : ProvenShape[Category] =
    (id, name, description, budgetId) <>(Category.tupled, Category.unapply)
}
