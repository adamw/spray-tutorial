package com.softwaremill.spray.server

import akka.actor.ActorSystem
import spray.routing._
import com.softwaremill.spray._
import spray.http.MediaTypes

object Step3Complete extends App with SimpleRoutingApp {
  implicit val actorSystem = ActorSystem()

  var plentyOfDwarfs = Dwarf.someDwarfs

  def getJson(route: Route) = get {
    respondWithMediaType(MediaTypes.`application/json`) { route }
  }

  startServer(interface = "localhost", port = 8080) {
    get {
      path("hello") { ctx =>
        ctx.complete("Welcome to the Land of Dwarfs!")
      }
    } ~
    getJson {
      path("list" / "all") {
        complete {
          Dwarf.toJson(plentyOfDwarfs)
        }
      }
    } ~
    getJson {
      path("dwarf" / IntNumber / "details") { index =>
        complete {
          Dwarf.toJson(plentyOfDwarfs(index))
        }
      }
    } ~
    post {
      path("dwarf" / "add" / "mining") {
        parameters("mineral"?, "gramsPerHour".as[Int]) { (mineral, gramsPerHour) =>
          val newDwarf = MiningDwarf(mineral.getOrElse("silver"), gramsPerHour)
          plentyOfDwarfs = newDwarf :: plentyOfDwarfs
          complete {
            "OK"
          }
        }
      }
    }
  }
}
