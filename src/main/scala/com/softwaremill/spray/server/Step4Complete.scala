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

  var plentyOfAmber = Amber.ambers
  implicit val timeout = Timeout(1.second)
  import actorSystem.dispatcher

  lazy val helloActor = actorSystem.actorOf(Props(new HelloActor()))
  lazy val miningActor = actorSystem.actorOf(Props(new MiningActor()))

  def getJson(route: Route) = get {
    respondWithMediaType(MediaTypes.`application/json`) { route }
  }

  lazy val amberRoute = {
    get {
      path("hello") { ctx =>
        helloActor ! ctx
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

  lazy val miningRoute = {
    get {
      path("mining" / "remaining") {
        complete {
          (miningActor ? RemainingMiningTime).mapTo[Int]
            .map(s => s"Your amber will be mined in $s")
        }
      }
    }
  }

  startServer(interface = "localhost", port = 8080) {
    amberRoute ~ miningRoute
  }

  class HelloActor extends Actor {
    override def receive = {
      case ctx: RequestContext => ctx.complete("Welcome to Amber Gold!")
    }
  }

  class MiningActor extends Actor {
    private val timeRemaining = 10

    override def receive = {
      case RemainingMiningTime => sender ! timeRemaining
    }
  }

  object RemainingMiningTime
}
