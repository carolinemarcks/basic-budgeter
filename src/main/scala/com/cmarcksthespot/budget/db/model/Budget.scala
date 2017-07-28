package com.cmarcksthespot.budget.db.model


import slick.driver.MySQLDriver.api._
import slick.lifted.ProvenShape

case class Budget(id: Int, name: String, saved: Int, perMonth: Int, cap: Int)

private[db] class Budgets(tag: Tag)
      extends Table[Budget](tag, "budgets") {
  def id: Rep[Int] = column[Int]("id", O.PrimaryKey)

  def name: Rep[String] = column[String]("name", O.Length(50, varying = true))

  def saved: Rep[Int] = column[Int]("saved")

  def perMonth: Rep[Int] = column[Int]("per_month")

  def cap: Rep[Int] = column[Int]("cap")

  def * : ProvenShape[Budget] =
    (id, name, saved, perMonth, cap) <>(Budget.tupled, Budget.unapply)
}
