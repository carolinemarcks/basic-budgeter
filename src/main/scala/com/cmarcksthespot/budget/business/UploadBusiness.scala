package com.cmarcksthespot.budget.business

import java.util.UUID

import com.cmarcksthespot.budget.db.model.Transaction

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class BoaCreditCardUploadBusiness(transactionBusiness: TransactionBusiness, allocationBusiness: AllocationBusiness, accountBusiness: AccountBusiness) {
  lazy val uncategorizedId = Await.result(allocationBusiness.getUncategorizedId(), Duration.Inf)
  lazy val accounts = accountBusiness.getAccounts()
  lazy val creditCardAccount = accounts.find(_.name == "Bank of America Credit Card").get
  lazy val checkingAccount = accounts.find(_.name == "Bank of America Checking").get
  lazy val savingsAccount = accounts.find(_.name == "Bank of America Savings").get

  def runUpload(): Future[Unit] = {
    val creditStatementFileNames = List(
      "new-credit.csv"
      /*
      "January2017_2537.csv",
      "February2017_2537.csv",
      "March2017_2537.csv",
      "April2017_2537.csv",
      "May2017_2537.csv",
      "June2017_2537.csv",
      "July2017_2537.csv"*/
    )


    val creditUpload = creditStatementFileNames.foldLeft(Future.successful(())) {
      case (prevFut, fileName) =>
        prevFut.flatMap { _ =>
          val path = filePath(fileName)
          println(s"uploading $fileName")
          uploadCreditTransactions(path)
        }
    }
    val checkingUpload =
      uploadAcctTransactions(filePath("new-checking.csv"), checkingAccount.id)
    val savingsUpload =
      uploadAcctTransactions(filePath("new-saving.csv"), savingsAccount.id)

    Future.sequence(Seq(creditUpload, checkingUpload, savingsUpload)).map { _ => }
  }
  private def filePath(name: String) = s"/Users/caroline/Downloads/$name"


  private implicit class BoaCreditTransactionConverter(b: BoaCreditCardTransaction) {
    def toTransactionRow = {
      val BoaCreditCardTransaction(postedDate, referenceNumber, payee, address, centsAmount) = b
      Transaction(
        id = referenceNumber,
        accountId = creditCardAccount.id,
        postedDate = new java.sql.Date(postedDate.getTime),
        payee = payee,
        address = address,
        amount = centsAmount,
        note = "",
        allocationId = uncategorizedId,
        isBalanced = false
      )
    }
  }
  private implicit class BoaAcctTransactionConverter(b: BoaAcctTransaction) {
    def toTransactionRow(acctId: Int) = {
      val BoaAcctTransaction(postedDate, description, centsAmount) = b
      Transaction(
        id = UUID.randomUUID().toString,
        accountId = acctId,
        postedDate = new java.sql.Date(postedDate.getTime),
        payee = description,
        address = "",
        amount = centsAmount,
        note = "",
        allocationId = uncategorizedId,
        isBalanced = false
      )
    }
  }
  private def uploadAcctTransactions(filePath: String, acctId: Int): Future[Unit] = {
    val source = scala.io.Source.fromFile(filePath)
    val boaTransactions = BoaCsvParse.parseAccountLines(source.getLines())
    val uploadFuture = transactionBusiness.insertTransactions(boaTransactions.map(_.toTransactionRow(acctId)).toList)
    uploadFuture.onComplete { _ => source.close }
    uploadFuture
  }

  private def uploadCreditTransactions(filePath: String): Future[Unit] = {
    val source = scala.io.Source.fromFile(filePath)
    val boaTransactions = BoaCsvParse.parseCreditCardLines(source.getLines())
      .toList.groupBy(_.referenceNumber).map {
        case (originalRef, transactions) =>
          transactions.partition(_.payee == "FOREIGN TRANSACTION FEE") match {
            case (Nil, original :: Nil) => original // TODO not an efficient way of matching out this case
            case (fee :: Nil, original :: Nil) =>
              original.copy(centsAmount = (original.centsAmount + fee.centsAmount))
            case _ => throw new RuntimeException(s"Two transactions used reference number $originalRef")
          }
      }

    val uploadFuture = transactionBusiness.insertTransactions(boaTransactions.map(_.toTransactionRow))
    uploadFuture.onComplete { _ => source.close }
    uploadFuture
  }
}
