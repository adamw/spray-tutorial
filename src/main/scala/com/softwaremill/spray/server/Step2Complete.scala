package com.softwaremill.spray.server

import akka.actor.ActorSystem
import spray.routing.SimpleRoutingApp
import com.softwaremill.spray.Dwarf
import spray.http.MediaTypes

object Step2Complete extends App with SimpleRoutingApp {
  implicit val actorSystem = ActorSystem()

  var plentyOfDwarfs = Dwarf.someDwarfs

  startServer(interface = "localhost", port = 8080) {
    get {
      path("hello") { ctx =>
        ctx.complete("Welcome to the Land of Dwarfs!")
      }
    } ~
    get {
      path("list" / "all") {
        respondWithMediaType(MediaTypes.`application/json`) {
          complete {
            Dwarf.toJson(plentyOfDwarfs)
          }
        }
      }
    }
  }
}
