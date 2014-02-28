package com.softwaremill.spray

trait Fish

case class Shark(fishEatenDaily: Long) extends Fish

case class Salmon(smoked: Boolean) extends Fish

case class Tuna(oceanOfOrigin: String, age: Int) extends Fish
