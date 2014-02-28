package com.softwaremill.spray.client

import spray.http._
import spray.client.pipelining._
import akka.actor.ActorSystem

object FishClient extends App {
  implicit val system = ActorSystem()
  import system.dispatcher

  val pipeline = sendReceive

  val securePipeline = addCredentials(BasicHttpCredentials("adam", "1234")) ~> sendReceive

  val result = securePipeline(Get("http://localhost:8080/list/all"))
  result.foreach { response =>
    println(s"Request completed with status ${response.status} and content:\n${response.entity.asString}")
  }

  pipeline(Post("http://localhost:8080/fish/add/tuna?ocean=x&age=10"))

  Thread.sleep(1000L)

  system.shutdown()
  system.awaitTermination()
}

