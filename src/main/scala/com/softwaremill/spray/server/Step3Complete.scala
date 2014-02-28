package com.softwaremill.spray.server

import akka.actor.ActorSystem
import spray.routing._
import com.softwaremill.spray._
import org.json4s.native.Serialization._
import org.json4s.native.Serialization
import spray.http.MediaTypes
import com.softwaremill.spray.Tuna
import com.softwaremill.spray.Salmon
import org.json4s.ShortTypeHints
import com.softwaremill.spray.Shark

object Step3Complete extends App with SimpleRoutingApp {
  implicit val actorSystem = ActorSystem()

  var plentyOfFish = List[Fish](
    Tuna(oceanOfOrigin = "atlantic", age = 3),
    Tuna(oceanOfOrigin = "pacific", age = 5),
    Salmon(smoked = false)
  )
  implicit val formats = Serialization.formats(ShortTypeHints(List(classOf[Shark], classOf[Salmon], classOf[Tuna])))

  def getJson(route: Route) = get { respondWithMediaType(MediaTypes.`application/json`) { route } }

  startServer(interface = "localhost", port = 8080) {
    get {
      path("hello") { ctx =>
        ctx.complete("Here's the list of fish in the aquarium")
      }
    } ~
    getJson {
      path("list" / "all") {
        complete {
          writePretty(plentyOfFish)
        }
      }
    } ~
    getJson {
      path("fish" / IntNumber / "details") { index =>
        complete {
          writePretty(plentyOfFish(index))
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
