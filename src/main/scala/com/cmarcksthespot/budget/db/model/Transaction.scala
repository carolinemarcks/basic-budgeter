package com.cmarcksthespot.budget.db.model

import java.sql.Date

import slick.driver.MySQLDriver.api._
import slick.lifted.ProvenShape

case class Transaction(id: String, accountId: Int, postedDate: Date, payee: String, address: String, centsAmount: Int, note: String)

private[db] class Transactions(tag: Tag)
      extends Table[Transaction](tag, "transactions") {
  def id: Rep[String] = column[String]("id", O.PrimaryKey, O.Length(50, varying = true)) //diff account types will have different lengths of id

  def accountId: Rep[Int] = column[Int]("account_id")

  def postedDate: Rep[Date] = column[Date]("posted_date")

  def payee: Rep[String] = column[String]("payee")

  def address: Rep[String] = column[String]("address")

  def centsAmount: Rep[Int] = column[Int]("cents_amount")

  def note: Rep[String] = column[String]("note")

  def account =
    foreignKey("account_fk", accountId, TableQuery[Accounts])(_.id)

  def * : ProvenShape[Transaction] =
    (id, accountId, postedDate, payee, address, centsAmount, note) <>(Transaction.tupled, Transaction.unapply)
}