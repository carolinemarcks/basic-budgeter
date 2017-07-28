package com.cmarcksthespot.budget.db.model


import slick.driver.MySQLDriver.api._
import slick.lifted.ProvenShape

sealed trait AllocationType { val id: Int }
case object Goal extends AllocationType { val id = 1 }
case object Budget extends AllocationType { val id = 2 }
object AllocationType {
  def fromId(id: Int): AllocationType = id match {
    case Goal.id => Goal
    case Budget.id => Budget
  }
}

case class Allocation(id: Int, name: String, saved: Int, weight: Int, cap: Int, allocationType: AllocationType)
object AllocationExtraction {
  def tupled(tup: (Int, String, Int, Int, Int, Int)): Allocation = {
    Allocation(tup._1, tup._2, tup._3, tup._4, tup._5, AllocationType.fromId(tup._6))
  }
  def unapply(in: Allocation): Option[(Int, String, Int, Int, Int, Int)] = {
    Some((in.id, in.name, in.saved, in.weight, in.cap, in.allocationType.id))
  }
}

private[db] class Allocations(tag: Tag)
      extends Table[Allocation](tag, "allocations") {
  def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def name: Rep[String] = column[String]("name", O.Length(50, varying = true))

  def saved: Rep[Int] = column[Int]("saved")

  def weight: Rep[Int] = column[Int]("weight")

  def cap: Rep[Int] = column[Int]("cap")

  def allocationType: Rep[Int] = column[Int]("allocation_type")

  def idx = index("idx_alloc_type", allocationType, unique = false)

  def * : ProvenShape[Allocation] =
    (id, name, saved, weight, cap, allocationType) <> (AllocationExtraction.tupled, AllocationExtraction.unapply)
}
