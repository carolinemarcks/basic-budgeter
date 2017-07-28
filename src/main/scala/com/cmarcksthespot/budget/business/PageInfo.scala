package com.cmarcksthespot.budget.business

import com.cmarcksthespot.budget.business.Parsers.{ParsedInt, ParsedLong}


case class PageInfo(millis: Long, offset: Int) {
  override def toString: String = {
    s"$millis:$offset"
  }
}

object PageInfo {
  def deserailize(str: String): PageInfo = {
    str.split(":") match {
      case Array(ParsedLong(epochMillis), ParsedInt(offset)) => PageInfo(epochMillis, offset)
    }
  }

  def prevPage(sortedDescendingMillis: List[Long], prevPage: Option[PageInfo], pageSize: Int) =
    sortedDescendingMillis.reverse match {
      case smallPage if smallPage.size < pageSize =>
        //TODO probably should avoid the reverse in this case
        None // don't need a prev page,
      case last :: rest => {
        Some(prevPage.filter(_.millis == last).map { currPageInfo =>
          currPageInfo.copy(offset = currPageInfo.offset + pageSize) //just need to increment offset by pagesize
        } getOrElse {
          PageInfo(last, rest.takeWhile(_.equals(last)).size + 1)
        })
      }
    }
}
