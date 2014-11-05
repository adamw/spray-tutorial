package com.softwaremill.spray.server

import akka.actor.ActorSystem
import spray.routing.SimpleRoutingApp
import com.softwaremill.spray.Amber
import spray.http.MediaTypes

object Step2Complete extends App with SimpleRoutingApp {
  implicit val actorSystem = ActorSystem()

  var plentyOfAmber = Amber.ambers

  startServer(interface = "localhost", port = 8080) {
    get {
      path("hello") { ctx =>
        ctx.complete("Welcome to Amber Gold!")
      }
    } ~
    get {
      path("list" / "all") {
        respondWithMediaType(MediaTypes.`application/json`) {
          complete {
            Amber.toJson(plentyOfAmber)
          }
        }
      }
    }
  }
}
