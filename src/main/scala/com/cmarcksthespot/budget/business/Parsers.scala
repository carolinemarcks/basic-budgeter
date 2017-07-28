package com.cmarcksthespot.budget.business

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

}
