package com.softwaremill.spray.server

import spray.routing.Directives

trait WaterLevelService {
  this: Directives =>

  lazy val waterLevelService = {
    get {
      path("waterlevel") {
        complete {
          "10"
        }
      }
    }
  }
}
