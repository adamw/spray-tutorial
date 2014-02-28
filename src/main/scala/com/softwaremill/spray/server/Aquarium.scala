package com.softwaremill.spray.server

import spray.routing.SimpleRoutingApp
import akka.actor.{Props, ActorSystem}

object Aquarium extends App with SimpleRoutingApp with FishService with WaterLevelService {
  implicit val actorSystem = ActorSystem("spray-demo")
  val actor = actorSystem.actorOf(Props(new AquariumActor()))

  startServer(interface = "localhost", port = 8080) {
    fishService ~ waterLevelService
  }
}
