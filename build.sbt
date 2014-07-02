name := "spray-tutorial"

version := "1.0"

scalaVersion := "2.11.1"

resolvers += "spray repo" at "http://repo.spray.io"

val sprayVersion = "1.3.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.4",
  "io.spray" %% "spray-routing" % sprayVersion,
  "io.spray" %% "spray-client" % sprayVersion,
  "io.spray" %% "spray-testkit" % sprayVersion % "test",
  "org.json4s" %% "json4s-native" % "3.2.9",
  "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "org.scalatest" %% "scalatest" % "2.1.3" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test"
)