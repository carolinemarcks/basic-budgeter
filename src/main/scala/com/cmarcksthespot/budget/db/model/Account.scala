package com.cmarcksthespot.budget.db.model

import slick.driver.MySQLDriver.api._
import slick.lifted.ProvenShape

case class Account(name: String, balance: Int)

private[db] class Accounts(tag: Tag) extends Table[Account](tag, "accounts") {
  def name: Rep[String] = column[String]("name", O.PrimaryKey)

  def balance: Rep[Int] = column[Int]("balance")

  def * : ProvenShape[Account] =
    (name, balance) <>(Account.tupled, Account.unapply)
}