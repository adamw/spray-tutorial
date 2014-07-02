package com.softwaremill.spray

import org.json4s.native.Serialization
import org.json4s.native.Serialization._
import org.json4s.ShortTypeHints

trait Dwarf

case class CookingDwarf(vegetarian: Boolean) extends Dwarf

case class MiningDwarf(mineral: String, gramsPerHour: Int) extends Dwarf

object Dwarf {
  val someDwarfs = List[Dwarf](
    MiningDwarf(mineral = "gold", gramsPerHour = 10),
    MiningDwarf(mineral = "coal", gramsPerHour = 3000),
    CookingDwarf(vegetarian = false)
  )

  private implicit val formats = Serialization.formats(ShortTypeHints(List(classOf[MiningDwarf], classOf[CookingDwarf])))
  def toJson(dwarf: List[Dwarf]): String = writePretty(dwarf)
  def toJson(dwarf: Dwarf): String = writePretty(dwarf)
}