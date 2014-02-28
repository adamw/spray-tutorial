name := "spray-tutorial"

version := "1.0"

scalaVersion := "2.10.3"

resolvers += "spray repo" at "http://repo.spray.io"

val sprayVersion = "1.2.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.2.3",
  "io.spray" % "spray-routing" % sprayVersion,
  "io.spray" % "spray-client" % sprayVersion,
  "io.spray" % "spray-testkit" % sprayVersion % "test",
  "org.json4s" %% "json4s-native" % "3.2.7",
  "com.typesafe" %% "scalalogging-slf4j" % "1.0.1",
  "ch.qos.logback" % "logback-classic" % "1.0.13",
  "org.scalatest" %% "scalatest" % "2.0" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test"
)