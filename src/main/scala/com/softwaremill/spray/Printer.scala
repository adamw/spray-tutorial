package com.softwaremill.spray

import org.json4s.native.Serialization
import org.json4s.native.Serialization._
import org.json4s.ShortTypeHints

trait Printer

case class LaserPrinter(color: Boolean) extends Printer

case class NeedlePrinter(manufacturer: String, pins: Int) extends Printer

object Printer {
  val somePrinters = List[Printer](
    NeedlePrinter(manufacturer = "Epson", pins = 12),
    NeedlePrinter(manufacturer = "Epson", pins = 24),
    LaserPrinter(color = false)
  )

  private implicit val formats = Serialization.formats(ShortTypeHints(List(classOf[LaserPrinter], classOf[NeedlePrinter])))
  def toJson(printer: List[Printer]): String = writePretty(printer)
  def toJson(printer: Printer): String = writePretty(printer)
}