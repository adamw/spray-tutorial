package com.softwaremill.spray.server

import akka.actor.{Props, Actor, ActorSystem}
import spray.routing._
import com.softwaremill.spray._
import spray.http.MediaTypes
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import com.softwaremill.spray.NeedlePrinter

object Step4Complete extends App with SimpleRoutingApp {
  implicit val actorSystem = ActorSystem()

  var plentyOfPrinters = Printer.somePrinters
  implicit val timeout = Timeout(1.second)
  import actorSystem.dispatcher

  val helloActor = actorSystem.actorOf(Props(new HelloActor()))
  val inkActor = actorSystem.actorOf(Props(new InkActor()))

  def getJson(route: Route) = get {
    respondWithMediaType(MediaTypes.`application/json`) { route }
  }

  lazy val printerRoute = {
    get {
      path("hello") { ctx =>
        helloActor ! ctx
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

  lazy val supplyRoute = {
    get {
      path("supply" / "ink") {
        complete {
          (inkActor ? GetInkSupply).mapTo[Int]
            .map(s => s"The supply of the ink is $s")
        }
      }
    }
  }

  startServer(interface = "localhost", port = 8080) {
    printerRoute ~ supplyRoute
  }

  class HelloActor extends Actor {
    override def receive = {
      case ctx: RequestContext => ctx.complete("Welcome to the Land of PrinTers (LPT)!")
    }
  }

  class InkActor extends Actor {
    private val inkSupply = 10

    override def receive = {
      case GetInkSupply => sender ! inkSupply
    }
  }

  object GetInkSupply
}
