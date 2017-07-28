package com.cmarcksthespot.budget.db.model

import slick.driver.MySQLDriver.api._
import slick.lifted.ProvenShape

case class Goal(id: Int, name: String, saved: Int, weight: Int, cap: Int)

private[db] class Goals(tag: Tag)
      extends Table[Goal](tag, "goals") {
  def id: Rep[Int] = column[Int]("id", O.PrimaryKey)

  def name: Rep[String] = column[String]("name", O.Length(50, varying = true))

  def saved: Rep[Int] = column[Int]("saved")

  def weight: Rep[Int] = column[Int]("weight")

  def cap: Rep[Int] = column[Int]("cap")

  def * : ProvenShape[Goal] =
    (id, name, saved, weight, cap) <>(Goal.tupled, Goal.unapply)
}
