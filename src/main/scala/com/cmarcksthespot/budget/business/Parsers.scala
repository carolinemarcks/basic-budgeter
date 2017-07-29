package com.cmarcksthespot.budget.business

import java.text.{ ParseException, SimpleDateFormat }
import java.util.Date

object Parsers {

  object ParsedLong {
    def unapply(arg: String): Option[Long] = try {
      Some(arg.toLong)
    } catch {
      case e: NumberFormatException => None
    }
  }

  object ParsedInt {
    def unapply(arg: String): Option[Int] = try {
      Some(arg.toInt)
    } catch {
      case e: NumberFormatException => None
    }
  }

  object ParsedDouble {
    def unapply(arg: String): Option[Double] = try {
      Some(arg.toDouble)
    } catch {
      case e: NumberFormatException => None
    }
  }
  object ParsedCents {
    def unapply(arg: String): Option[Int] = try {
      arg.split('.') match {
        case Array(ParsedInt(dollars), ParsedInt(cents)) =>
          val absDollars = Math.abs(dollars)
          val tot = absDollars * 100 + cents
          if (absDollars == dollars) Some(tot)
          else Some(tot * -1)
        case _ => None
      }
    } catch {
      case e: NumberFormatException => None
    }
  }

  object ParsedSimpleDate {
    val pattern = "MM/dd/yyyy"
    val simpleDateFormat = new SimpleDateFormat(pattern)
    def unapply(arg: String): Option[Date] = try {
      Some(simpleDateFormat.parse(arg))
    } catch {
      case e: ParseException => None
    }
  }

  object ParsedCSV {
    def unapply(arg: String): Option[Array[String]] = {
      Some(arg.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1).map(_.trim))
    }

  }
}
