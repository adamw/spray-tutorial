package com.softwaremill.spray.server

import akka.actor.ActorSystem
import spray.routing.SimpleRoutingApp
import com.softwaremill.spray.Fish
import org.json4s.native.Serialization
import org.json4s.native.Serialization._
import com.softwaremill.spray.Tuna
import com.softwaremill.spray.Salmon
import org.json4s.ShortTypeHints
import com.softwaremill.spray.Shark
import spray.http.MediaTypes

object Step2Complete extends App with SimpleRoutingApp {
  implicit val actorSystem = ActorSystem()

  var plentyOfFish = List[Fish](
    Tuna(oceanOfOrigin = "atlantic", age = 3),
    Tuna(oceanOfOrigin = "pacific", age = 5),
    Salmon(smoked = false)
  )
  implicit val formats = Serialization.formats(ShortTypeHints(List(classOf[Shark], classOf[Salmon], classOf[Tuna])))

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
            writePretty(plentyOfFish)
          }
        }
      }
    }
  }
}
