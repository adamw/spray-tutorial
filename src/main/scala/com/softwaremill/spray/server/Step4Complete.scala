package com.softwaremill.spray.server

import akka.actor.{Props, Actor, ActorSystem}
import spray.routing.SimpleRoutingApp
import com.softwaremill.spray._
import org.json4s.native.Serialization._
import org.json4s.native.Serialization
import com.softwaremill.spray.Tuna
import com.softwaremill.spray.Salmon
import org.json4s.ShortTypeHints
import spray.http.MediaTypes
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

object Step4Complete extends App with SimpleRoutingApp {
  implicit val actorSystem = ActorSystem()

  var plentyOfFish = List[Fish](
    Tuna(oceanOfOrigin = "atlantic", age = 3),
    Tuna(oceanOfOrigin = "pacific", age = 5),
    Salmon(smoked = false)
  )
  implicit val formats = Serialization.formats(ShortTypeHints(List(classOf[Shark], classOf[Salmon], classOf[Tuna])))
  implicit val timeout = Timeout(1.second)
  import actorSystem.dispatcher

  val waterLevelActor = actorSystem.actorOf(Props(new WaterLevelActor()))

  lazy val fishRoutes = {
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
    } ~
    get {
      path("fish" / IntNumber / "details") { index =>
        respondWithMediaType(MediaTypes.`application/json`) {
          complete {
            writePretty(plentyOfFish(index))
          }
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

  lazy val waterRoutes = {
    get {
      path("waterlevel") {
        complete {
          (waterLevelActor ? GetWaterLevel).mapTo[Int].map(wl => s"The water level is $wl")
        }
      }
    }
  }

  startServer(interface = "localhost", port = 8080) {
    fishRoutes ~ waterRoutes
  }

  class WaterLevelActor extends Actor {
    private val waterLevel = 10

    override def receive = {
      case GetWaterLevel => sender ! waterLevel
    }
  }

  object GetWaterLevel
}
