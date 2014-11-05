package com.softwaremill.spray.server

import akka.actor.ActorSystem
import akka.http.Http
import akka.io.IO
import akka.pattern.ask
import akka.stream.FlowMaterializer
import akka.util.Timeout
import akka.http.routing.ScalaRoutingDSL._
import scala.concurrent.duration._

object AkkaHttpPreview extends App {
  implicit val system = ActorSystem("ServerTest")
  import system.dispatcher
  implicit val materializer = FlowMaterializer()

  implicit val askTimeout: Timeout = 500.millis
  val bindingFuture = (IO(Http) ?
    Http.Bind(interface = "localhost", port = 8080))
    .mapTo[Http.ServerBinding]

  handleConnections(bindingFuture) withRoute {
    get {
      path("hello") {
        complete {
          "Welcome to Amber Gold!"
        }
      }
    }
  }
}
