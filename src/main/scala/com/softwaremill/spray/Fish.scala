package com.softwaremill.spray

import org.json4s.native.Serialization
import org.json4s.native.Serialization._
import org.json4s.ShortTypeHints

trait Fish

case class Shark(fishEatenDaily: Long) extends Fish

case class Salmon(smoked: Boolean) extends Fish

case class Tuna(oceanOfOrigin: String, age: Int) extends Fish

object Fish {
  val someFish = List[Fish](
    Tuna(oceanOfOrigin = "atlantic", age = 3),
    Tuna(oceanOfOrigin = "pacific", age = 5),
    Salmon(smoked = false)
  )

  private implicit val formats = Serialization.formats(ShortTypeHints(List(classOf[Shark], classOf[Salmon], classOf[Tuna])))
  def toJson(fish: List[Fish]): String = writePretty(fish)
  def toJson(fish: Fish): String = writePretty(fish)
}