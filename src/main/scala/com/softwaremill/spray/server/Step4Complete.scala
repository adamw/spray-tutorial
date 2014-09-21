package com.softwaremill.spray.server

import akka.actor.{Props, Actor, ActorSystem}
import spray.routing._
import com.softwaremill.spray._
import spray.http.MediaTypes
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

object Step4Complete extends App with SimpleRoutingApp {
  implicit lazy val actorSystem = ActorSystem()

  var plentyOfSilicon = Silicon.silicons
  implicit val timeout = Timeout(1.second)
  import actorSystem.dispatcher

  lazy val helloActor = actorSystem.actorOf(Props(new HelloActor()))
  lazy val burnActor = actorSystem.actorOf(Props(new BurnActor()))

  def getJson(route: Route) = get {
    respondWithMediaType(MediaTypes.`application/json`) { route }
  }

  lazy val siliconRoute = {
    get {
      path("hello") { ctx =>
        helloActor ! ctx
      }
    } ~
    getJson {
      path("list" / "all") {
        complete {
          Silicon.toJson(plentyOfSilicon)
        }
      }
    } ~
    getJson {
      path("silicon" / IntNumber / "details") { index =>
        complete {
          Silicon.toJson(plentyOfSilicon(index))
        }
      }
    } ~
    post {
      path("silicon" / "add" / "mining") {
        parameters("name"?, "grainSize".as[Int]) { (name, grainSize) =>
          val newSilicon = MultiCrystalSilicon(name.getOrElse("Microcrystalline"), grainSize)
          plentyOfSilicon = newSilicon :: plentyOfSilicon
          complete {
            "OK"
          }
        }
      }
    }
  }

  lazy val burnRoute = {
    get {
      path("burn" / "remaining") {
        complete {
          (burnActor ? RemainingBurningTime).mapTo[Int]
            .map(s => s"Your silicon will be ready in $s")
        }
      }
    }
  }

  startServer(interface = "localhost", port = 8080) {
    siliconRoute ~ burnRoute
  }

  class HelloActor extends Actor {
    override def receive = {
      case ctx: RequestContext => ctx.complete("Welcome to the Silicon Valley!")
    }
  }

  class BurnActor extends Actor {
    private val timeRemaining = 10

    override def receive = {
      case RemainingBurningTime => sender ! timeRemaining
    }
  }

  object RemainingBurningTime
}
