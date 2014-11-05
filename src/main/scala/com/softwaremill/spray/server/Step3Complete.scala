package com.softwaremill.spray.server

import akka.actor.ActorSystem
import spray.routing._
import com.softwaremill.spray._
import spray.http.MediaTypes

object Step3Complete extends App with SimpleRoutingApp {
  implicit val actorSystem = ActorSystem()

  var plentyOfAmber = Amber.ambers

  def getJson(route: Route) = get {
    respondWithMediaType(MediaTypes.`application/json`) { route }
  }

  startServer(interface = "localhost", port = 8080) {
    get {
      path("hello") { ctx =>
        ctx.complete("Welcome to Amber Gold!")
      }
    } ~
    getJson {
      path("list" / "all") {
        complete {
          Amber.toJson(plentyOfAmber)
        }
      }
    } ~
    getJson {
      path("amber" / IntNumber / "details") { index =>
        complete {
          Amber.toJson(plentyOfAmber(index))
        }
      }
    } ~
    post {
      path("amber" / "add" / "mined") {
        parameters("country"?, "size".as[Int]) { (country, size) =>
          val newAmber = MinedAmber(country.getOrElse("Estonia"), size)
          plentyOfAmber = newAmber :: plentyOfAmber
          complete {
            "OK"
          }
        }
      }
    }
  }
}
