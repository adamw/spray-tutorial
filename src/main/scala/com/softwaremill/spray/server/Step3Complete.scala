package com.softwaremill.spray.server

import akka.actor.ActorSystem
import spray.routing._
import com.softwaremill.spray._
import spray.http.MediaTypes
import com.softwaremill.spray.NeedlePrinter

object Step3Complete extends App with SimpleRoutingApp {
  implicit val actorSystem = ActorSystem()

  var plentyOfPrinters = Printer.somePrinters

  def getJson(route: Route) = get {
    respondWithMediaType(MediaTypes.`application/json`) { route }
  }

  startServer(interface = "localhost", port = 8080) {
    get {
      path("hello") { ctx =>
        ctx.complete("Welcome to the Land of PrinTers (LPT)!")
      }
    } ~
    getJson {
      path("list" / "all") {
        complete {
          Printer.toJson(plentyOfPrinters)
        }
      }
    } ~
    getJson {
      path("printer" / IntNumber / "details") { index =>
        complete {
          Printer.toJson(plentyOfPrinters(index))
        }
      }
    } ~
    post {
      path("printer" / "add" / "needle") {
        parameters("manufacturer"?, "pins".as[Int]) { (manufacturer, pins) =>
          val newPrinter = NeedlePrinter(manufacturer.getOrElse("Epson"), pins)
          plentyOfPrinters = newPrinter :: plentyOfPrinters
          complete {
            "OK"
          }
        }
      }
    }
  }
}
