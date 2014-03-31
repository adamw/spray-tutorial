package com.softwaremill.spray.server

import akka.actor.{Props, Actor, ActorSystem}
import spray.routing._
import com.softwaremill.spray._
import spray.http.MediaTypes
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import com.softwaremill.spray.Tuna

object Step4Complete extends App with SimpleRoutingApp {
  implicit val actorSystem = ActorSystem()

  var plentyOfFish = Fish.someFish
  implicit val timeout = Timeout(1.second)
  import actorSystem.dispatcher

  val waterLevelActor = actorSystem.actorOf(Props(new WaterLevelActor()))

  def getJson(route: Route) = get {
    respondWithMediaType(MediaTypes.`application/json`) { route }
  }

  lazy val fishRoute = {
    get {
      path("hello") { ctx =>
        ctx.complete("Here's the list of fish in the aquarium")
      }
    } ~
    getJson {
      path("list" / "all") {
        complete {
          Fish.toJson(plentyOfFish)
        }
      }
    } ~
      getJson {
      path("fish" / IntNumber / "details") { index =>
        complete {
          Fish.toJson(plentyOfFish(index))
        }
      }
    } ~
    post {
      path("fish" / "add" / "tuna") {
        parameters("ocean"?, "age".as[Int]) { (ocean, age) =>
          val newTuna = Tuna(ocean.getOrElse("pacific"), age)
          plentyOfFish = newTuna :: plentyOfFish
          complete {
            "OK"
          }
        }
      }
    }
  }

  lazy val waterRoute = {
    get {
      path("waterlevel") {
        complete {
          (waterLevelActor ? GetWaterLevel).mapTo[Int]
            .map(wl => s"The water level is $wl")
        }
      }
    }
  }

  startServer(interface = "localhost", port = 8080) {
    fishRoute ~ waterRoute
  }

  class WaterLevelActor extends Actor {
    private val waterLevel = 10

    override def receive = {
      case GetWaterLevel => sender ! waterLevel
    }
  }

  object GetWaterLevel
}
