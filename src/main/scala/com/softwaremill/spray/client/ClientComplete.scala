package com.softwaremill.spray.client

import spray.http._
import spray.client.pipelining._
import akka.actor.ActorSystem

object ClientComplete extends App {
  implicit val system = ActorSystem()
  import system.dispatcher

  val pipeline = sendReceive

  val securePipeline = addCredentials(BasicHttpCredentials("adam", "1234")) ~> sendReceive

  val result = securePipeline(Get("http://localhost:8080/list/all"))
  result.foreach { response =>
    println(s"Request completed with status ${response.status} and content:\n${response.entity.asString}")
  }

  pipeline(Post("http://localhost:8080/printer/add/needle?manufacturer=Canon&pins=10"))

  Thread.sleep(1000L)

  system.shutdown()
  system.awaitTermination()
}

