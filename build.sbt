ThisBuild / organization := "mdaros.labs.kafka"
ThisBuild / version := "1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.0"

lazy val `wh-streams-ksfka-scala` = ( project in file ( "." ) )
  .aggregate ( `aggregator`, `mqtt-kafka-bridge`, `data-model`, `devices-metrics-simulator` )
  .settings ( name := "wh-streams-kafka-scala" )

val akkaVersion = "2.6.14"
val jacksonVersion = "2.12.4"
val kafkaVersion = "2.8.1"
val circeVersion = "0.14.1"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.5.2" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.2.9" % Test

lazy val `mqtt-kafka-bridge` = ( project in file ( "mqtt-kafka-bridge" ) )
  .settings ( libraryDependencies ++=  Seq (
    "com.lightbend.akka" %% "akka-stream-alpakka-mqtt" % "3.0.4",
    "com.typesafe.akka" %% "akka-stream-kafka" % "2.1.1",
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
    macwire,
    scalaTest,
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
    "org.mockito" %% "mockito-scala" % "1.16.49" % Test
  ) )

lazy val `data-model` = ( project in file ( "data-model" ) )
  .settings ( libraryDependencies ++=  Seq ( scalaTest ) )

lazy val `devices-metrics-simulator` = ( project in file ( "devices-metrics-simulator" ) )
  .dependsOn ( `data-model` )
  .settings ( libraryDependencies ++=  Seq (
    "com.lightbend.akka" %% "akka-stream-alpakka-mqtt" % "3.0.4",
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion,
    "com.fasterxml.jackson.module" % "jackson-module-parameter-names" % jacksonVersion,
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % jacksonVersion,
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % jacksonVersion,
    "mdaros.labs.kafka" %% "data-model" % "1.0-SNAPSHOT",
    macwire,
    scalaTest,
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
    "org.mockito" %% "mockito-scala" % "1.16.49" % Test
  ) )

lazy val `aggregator` = ( project in file ( "aggregator" ) )
  .dependsOn ( `data-model` )
  .settings (
    libraryDependencies ++=  Seq (
      "org.apache.kafka" % "kafka-clients" % kafkaVersion,
      "org.apache.kafka" % "kafka-streams" % kafkaVersion,
      "org.apache.kafka" %% "kafka-streams-scala" % kafkaVersion,

      "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion,
      "com.fasterxml.jackson.module" % "jackson-module-parameter-names" % jacksonVersion,
      "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % jacksonVersion,
      "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % jacksonVersion,

      // TODO Try to use Circe to handle JSON serialization / deserialization
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,

      "org.slf4j" % "slf4j-api" % "1.7.32",
      "ch.qos.logback" % "logback-core" % "1.2.10",
      "ch.qos.logback" % "logback-classic" % "1.2.10",
      "com.typesafe" % "config" % "1.4.1",
      macwire,

      // Test dependencies
      scalaTest,
      "org.mockito" %% "mockito-scala" % "1.16.49" % Test
  ) )