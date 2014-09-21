package com.softwaremill.spray.server

import akka.actor.ActorSystem
import spray.routing._
import com.softwaremill.spray._
import spray.http.MediaTypes

object Step3Complete extends App with SimpleRoutingApp {
  implicit val actorSystem = ActorSystem()

  var plentyOfSilicon = Silicon.silicons

  def getJson(route: Route) = get {
    respondWithMediaType(MediaTypes.`application/json`) { route }
  }

  startServer(interface = "localhost", port = 8080) {
    get {
      path("hello") { ctx =>
        ctx.complete("Welcome to the Silicon Valley!")
      }
    } ~
    getJson {
      path("list" / "all") {
        complete {
          Silicon.toJson(plentyOfSilicon)
        }
      }
    } ~
    getJson {
      path("silicon" / IntNumber / "details") { index =>
        complete {
          Silicon.toJson(plentyOfSilicon(index))
        }
      }
    } ~
    post {
      path("silicon" / "add" / "mining") {
        parameters("name"?, "grainSize".as[Int]) { (name, grainSize) =>
          val newSilicon = MultiCrystalSilicon(name.getOrElse("Microcrystalline"), grainSize)
          plentyOfSilicon = newSilicon :: plentyOfSilicon
          complete {
            "OK"
          }
        }
      }
    }
  }
}
