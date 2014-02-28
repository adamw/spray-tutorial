package com.softwaremill.spray.server

import com.softwaremill.spray.{Salmon, Shark, Fish, Tuna}
import org.json4s.native.Serialization._
import spray.http.MediaTypes
import spray.routing._
import akka.actor.{ActorSystem, ActorRef}
import akka.util.Timeout
import scala.concurrent.duration._
import akka.pattern.ask
import org.json4s.native.Serialization
import org.json4s.ShortTypeHints

trait FishService {
  this: Directives =>

  def actor: ActorRef
  def actorSystem: ActorSystem

  implicit val timeout = Timeout(1.second)

  def getJson(route: Route) = get { respondWithMediaType(MediaTypes.`application/json`) { route } }

  implicit val formats = Serialization.formats(ShortTypeHints(List(classOf[Shark], classOf[Salmon], classOf[Tuna])))

  lazy val fishService = {
    implicit val ec = actorSystem.dispatcher

    path("list" / "all") {
      getJson {
        complete {
          (actor ? ListAll).mapTo[List[Fish]].map(l => writePretty(l))
        }
      }
    } ~
      path("fish" / IntNumber / "details") { index =>
        get {
          respondWithMediaType(MediaTypes.`application/json`) {
            complete {
              (actor ? ListAll).mapTo[List[Fish]].map(_.apply(index)).map(f => writePretty(f))
            }
          }
        }
      } ~
      path("fish" / "add" / "tuna") {
        post {
          parameters("ocean"?, "age".as[Int]) { (ocean, age) =>
            val newTuna = Tuna(ocean.getOrElse("pacific"), age)
            complete {
              (actor ? newTuna).mapTo[String]
            }
          }
        }
      }
  }
}
