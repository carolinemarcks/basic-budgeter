package com.cmarcksthespot.budget.business

import com.cmarcksthespot.budget.db.model.Transaction

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class BoaCreditCardUploadBusiness(transactionBusiness: TransactionBusiness, allocationBusiness: AllocationBusiness, accountBusiness: AccountBusiness) {
  lazy val uncategorizedId = Await.result(allocationBusiness.getUncategorizedId(), Duration.Inf)
  lazy val boaAccount = accountBusiness.getAccounts().find(_.name == "Bank of America Credit Card").get

  def runUpload(): Future[Unit] = {
    val fileNames = List(
      "January2017_2537.csv",
      "February2017_2537.csv",
      "March2017_2537.csv",
      "April2017_2537.csv",
      "May2017_2537.csv",
      "June2017_2537.csv",
      "July2017_2537.csv"
    )


    fileNames.foldLeft(Future.successful(())) {
      case (prevFut, fileName) =>
        prevFut.flatMap { _ =>
          val filePath = s"/Users/caroline/Downloads/$fileName"
          println(s"uploading $fileName")
          uploadTransactions(filePath)
        }
    }
  }


  private implicit class BoaTransactionConverter(b: BoaTransaction) {
    def toTransactionRow = {
      val BoaTransaction(postedDate, referenceNumber, payee, address, centsAmount) = b
      Transaction(
        id = referenceNumber,
        accountId = boaAccount.id,
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

  private def uploadTransactions(filePath: String): Future[Unit] = {
    val source = scala.io.Source.fromFile(filePath)
    val boaTransactions = BoaCsvParse.parse(source.getLines())
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
