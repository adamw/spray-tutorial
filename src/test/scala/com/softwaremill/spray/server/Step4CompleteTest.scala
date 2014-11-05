package com.softwaremill.spray.server

import org.scalatest.{FlatSpec, ShouldMatchers}
import spray.testkit.ScalatestRouteTest
import spray.routing.Directives

class Step4CompleteTest extends FlatSpec with ShouldMatchers
  with ScalatestRouteTest with Directives {

  it should "work" in {
    Get("/hello") ~> Step4Complete.amberRoute ~> check {
      responseAs[String] should include ("Amber")
    }
  }
}
