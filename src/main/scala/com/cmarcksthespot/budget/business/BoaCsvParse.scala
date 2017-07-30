package com.cmarcksthespot.budget.business

import java.util.Date

import com.cmarcksthespot.budget.business.Parsers.{ParseQuoted, ParsedCSV, ParsedCents, ParsedSimpleDate}

case class BoaCreditCardTransaction(postedDate: Date,
                                    referenceNumber: String, // too big to fit in an int or logn
                                    payee: String,
                                    address: String,
                                    centsAmount: Int)
case class BoaAcctTransaction(postedDate: Date,
                              description: String,
                              centsAmount: Int)

object BoaCsvParse {
  def parseCreditCardLines(sourceLines: Iterator[String]): Iterator[BoaCreditCardTransaction] = {
    sourceLines.drop(1).map {
      case ParsedCSV(Array(ParsedSimpleDate(postedDate), refNumber, payee, address, ParsedCents(amount))) =>
        BoaCreditCardTransaction(postedDate, refNumber, clean(payee), clean(address), amount)
    }
  }

  def parseAccountLines(sourceLines: Iterator[String]): Iterator[BoaAcctTransaction] = {
    sourceLines.flatMap {
      case ParsedCSV(Array(ParsedSimpleDate(postedDate), ParseQuoted(description), ParseQuoted(ParsedCents(amount)), ParseQuoted(ParsedCents(runningBal)))) =>
        Some(BoaAcctTransaction(postedDate, clean(description), amount))
      case l => println(s"couldn't parse line $l")
        None
    }
  }

  //https://stackoverflow.com/questions/17995260/trimming-strings-in-scala
  val bad = "\" "
  def clean(s: String): String = {

    @scala.annotation.tailrec def start(n: Int): String =
      if (n == s.length) ""
      else if (bad.indexOf(s.charAt(n)) < 0) end(n, s.length)
      else start(1 + n)

    @scala.annotation.tailrec def end(a: Int, n: Int): String =
      if (n <= a) s.substring(a, n)
      else if (bad.indexOf(s.charAt(n - 1)) < 0) s.substring(a, n)
      else end(a, n - 1)

    start(0)
  }
}
