package com.softwaremill.spray.server

import org.scalatest.{FlatSpec, ShouldMatchers}
import spray.testkit.ScalatestRouteTest
import spray.routing.Directives

class WaterLevelServiceTest extends FlatSpec with ShouldMatchers
  with ScalatestRouteTest with WaterLevelService with Directives {

  it should "get the water level" in {
    Get("/waterlevel") ~> waterLevelService ~> check {
      responseAs[String] should be ("10")
    }
  }
}
