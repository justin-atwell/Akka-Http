name := "testRestApiService"
version := "1.0"
scalaVersion := "2.12.2"

val akkaHttpV = "10.0.5"
libraryDependencies ++= {
  Seq(
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "com.typesafe.slick" %% "slick" % "3.2.0",
    "org.slf4j" % "slf4j-nop" % "1.7.10",
    "com.h2database" % "h2" % "1.4.193",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV % "test",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test"
  )
}