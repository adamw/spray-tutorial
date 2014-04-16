package com.softwaremill.spray.server

import akka.actor.ActorSystem
import spray.routing.SimpleRoutingApp

object Step1Complete extends App with SimpleRoutingApp {
  implicit val actorSystem = ActorSystem()

  startServer(interface = "localhost", port = 8080) {
    get {
      path("hello") {
        complete {
          "Welcome to the Land of PrinTers (LPT)!"
        }
      }
    }
  }
}
