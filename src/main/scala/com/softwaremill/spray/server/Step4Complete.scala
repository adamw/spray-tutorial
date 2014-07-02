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

  var plentyOfDwarfs = Dwarf.someDwarfs
  implicit val timeout = Timeout(1.second)
  import actorSystem.dispatcher

  lazy val helloActor = actorSystem.actorOf(Props(new HelloActor()))
  lazy val foodActor = actorSystem.actorOf(Props(new FoodActor()))

  def getJson(route: Route) = get {
    respondWithMediaType(MediaTypes.`application/json`) { route }
  }

  lazy val dwarfRoute = {
    get {
      path("hello") { ctx =>
        helloActor ! ctx
      }
    } ~
    getJson {
      path("list" / "all") {
        complete {
          Dwarf.toJson(plentyOfDwarfs)
        }
      }
    } ~
    getJson {
      path("dwarf" / IntNumber / "details") { index =>
        complete {
          Dwarf.toJson(plentyOfDwarfs(index))
        }
      }
    } ~
    post {
      path("dwarf" / "add" / "mining") {
        parameters("mineral"?, "gramsPerHour".as[Int]) { (mineral, gramsPerHour) =>
          val newDwarf = MiningDwarf(mineral.getOrElse("silver"), gramsPerHour)
          plentyOfDwarfs = newDwarf :: plentyOfDwarfs
          complete {
            "OK"
          }
        }
      }
    }
  }

  lazy val supplyRoute = {
    get {
      path("supply" / "food") {
        complete {
          (foodActor ? GetFoodSupply).mapTo[Int]
            .map(s => s"The supply of the food is $s")
        }
      }
    }
  }

  startServer(interface = "localhost", port = 8080) {
    dwarfRoute ~ supplyRoute
  }

  class HelloActor extends Actor {
    override def receive = {
      case ctx: RequestContext => ctx.complete("Welcome to the Land of Dwarfs!")
    }
  }

  class FoodActor extends Actor {
    private val foodSupply = 10

    override def receive = {
      case GetFoodSupply => sender ! foodSupply
    }
  }

  object GetFoodSupply
}
