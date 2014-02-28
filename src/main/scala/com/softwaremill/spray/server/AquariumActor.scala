package com.softwaremill.spray.server

import akka.actor.Actor
import com.softwaremill.spray.{Salmon, Tuna, Fish}

class AquariumActor extends Actor {
  var plentyOfFish = List[Fish](
    Tuna(oceanOfOrigin = "atlantic", age = 3),
    Tuna(oceanOfOrigin = "pacific", age = 5),
    Salmon(smoked = false)
  )

  override def receive = {
    case ListAll => sender ! plentyOfFish
    case f: Fish => {
      plentyOfFish = f :: plentyOfFish
      sender ! "OK"
    }
  }
}

object ListAll
