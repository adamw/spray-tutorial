package com.softwaremill.spray.server

import akka.actor.ActorSystem
import spray.routing._
import com.softwaremill.spray._
import spray.http.MediaTypes
import com.softwaremill.spray.Tuna

object Step3Complete extends App with SimpleRoutingApp {
  implicit val actorSystem = ActorSystem()

  var plentyOfFish = Fish.someFish

  def getJson(route: Route) = get {
    respondWithMediaType(MediaTypes.`application/json`) { route }
  }

  startServer(interface = "localhost", port = 8080) {
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
}
