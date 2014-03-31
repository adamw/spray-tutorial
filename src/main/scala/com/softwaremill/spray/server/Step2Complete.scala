package com.softwaremill.spray.server

import akka.actor.ActorSystem
import spray.routing.SimpleRoutingApp
import com.softwaremill.spray.Fish
import spray.http.MediaTypes

object Step2Complete extends App with SimpleRoutingApp {
  implicit val actorSystem = ActorSystem()

  var plentyOfFish = Fish.someFish

  startServer(interface = "localhost", port = 8080) {
    get {
      path("hello") { ctx =>
        ctx.complete("Here's the list of fish in the aquarium")
      }
    } ~
    get {
      path("list" / "all") {
        respondWithMediaType(MediaTypes.`application/json`) {
          complete {
            Fish.toJson(plentyOfFish)
          }
        }
      }
    }
  }
}
