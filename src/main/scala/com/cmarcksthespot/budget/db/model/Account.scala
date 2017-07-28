package com.cmarcksthespot.budget.db.model

import slick.driver.MySQLDriver.api._
import slick.lifted.ProvenShape

case class Account(id: Int, name: String, balance: Int)

private[db] class Accounts(tag: Tag) extends Table[Account](tag, "accounts") {
  def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def name: Rep[String] = column[String]("name", O.Length(50, varying = true))

  def balance: Rep[Int] = column[Int]("balance")

  def idx = index("idx_a", name, unique = true)

  def * : ProvenShape[Account] =
    (id, name, balance) <>(Account.tupled, Account.unapply)
}