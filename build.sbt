ThisBuild / organization := "mdaros.labs.kafka"
ThisBuild / version := "1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.0"

lazy val `wh-streams-ksfka-scala` = ( project in file ( "." ) )
  .aggregate ( `mqtt-kafka-bridge`, `data-model`, `devices-metrics-simulator` )
  .settings ( name := "wh-streams-kafka-scala" )

val AkkaVersion = "2.6.14"
val JacksonVersion = "2.12.4"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.3" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.1.1" % Test

lazy val `mqtt-kafka-bridge` = ( project in file ( "mqtt-kafka-bridge" ) )
  .settings ( libraryDependencies ++=  Seq (
    "com.lightbend.akka" %% "akka-stream-alpakka-mqtt" % "3.0.2",
    "com.typesafe.akka" %% "akka-stream-kafka" % "2.1.0",
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.fasterxml.jackson.core" % "jackson-databind" % JacksonVersion,
    macwire,
    scalaTest,
    "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test,
    "org.mockito" %% "mockito-scala" % "1.16.37" % Test
  ) )

lazy val `data-model` = ( project in file ( "data-model" ) )
  .settings ( libraryDependencies ++=  Seq ( scalaTest ) )

lazy val `devices-metrics-simulator` = ( project in file ( "devices-metrics-simulator" ) )
  .dependsOn ( `data-model` )
  .settings ( libraryDependencies ++=  Seq (
    "com.lightbend.akka" %% "akka-stream-alpakka-mqtt" % "3.0.2",
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % JacksonVersion,
    "com.fasterxml.jackson.module" % "jackson-module-parameter-names" % JacksonVersion,
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % JacksonVersion,
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % JacksonVersion,
    "mdaros.labs.kafka" %% "data-model" % "1.0-SNAPSHOT",
    macwire,
    scalaTest,
    "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test,
    "org.mockito" %% "mockito-scala" % "1.16.37" % Test
  ) )