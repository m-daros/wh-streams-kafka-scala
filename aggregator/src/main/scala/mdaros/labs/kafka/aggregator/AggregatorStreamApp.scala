package mdaros.labs.kafka.aggregator

import com.fasterxml.jackson.databind.{ ObjectMapper, SerializationFeature }
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.scala.{ DefaultScalaModule, ScalaObjectMapper }
import com.typesafe.config.ConfigFactory
import mdaros.labs.kafka.aggregator.config.ConfigurationKeys
import mdaros.labs.kafka.aggregator.serde.CustomSerdes.{ AggregatedMeasureSerde, MeasureSerde }
import mdaros.labs.kafka.model.Measure
import org.apache.kafka.streams.kstream.TimeWindows
import org.apache.kafka.streams.{ KafkaStreams, StreamsConfig, Topology }
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.kstream.KStream
import org.apache.kafka.streams.scala.serialization.Serdes
import org.apache.kafka.streams.scala.serialization.Serdes._
import org.slf4j.LoggerFactory

import java.time.{ Duration, ZonedDateTime }
import java.util.Properties

object AggregatorStreamApp extends App {

  val logger = LoggerFactory.getLogger ( AggregatorStreamApp.getClass )

  val CONFIG_FILE_NAME = "application.conf"
  val SERVICE_NAME     = "aggregator"

  val config = ConfigFactory.load ( CONFIG_FILE_NAME )
    .getConfig ( SERVICE_NAME )

  val props = new Properties ()
  props.put ( StreamsConfig.APPLICATION_ID_CONFIG, config.getString ( ConfigurationKeys.APPLICATION_ID.key ) )
  props.put ( StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, config.getString ( ConfigurationKeys.BOOTSTRAP_SERVERS.key ) )
  props.put ( StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.stringSerde.getClass )
  props.put ( StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.stringSerde.getClass )

  // TODO Try Circe to handle JSON serialization / deserialization
  val jsonMapper = new ObjectMapper () with ScalaObjectMapper
  jsonMapper.registerModule ( DefaultScalaModule )
  jsonMapper.registerModule ( new JavaTimeModule )
  jsonMapper.disable ( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS )

  implicit val measureSerde = new MeasureSerde ()
  implicit val aggregatedMeasureSerde = new AggregatedMeasureSerde ()

  val builder = new StreamsBuilder ()

  val metrics: KStream [String, String] = builder.stream [String, String] ( config.getString ( ConfigurationKeys.METRICS.key ) )

  // Hourly aggregation
  val hourlyAggregated = metrics.flatMapValues ( rawValue => jsonMapper.readValue [ Seq [ Measure ]] ( rawValue ) )
    .selectKey ( ( _, measure ) => measure.deviceId + "-" + measure.metric )
    .mapValues ( measure => new AggregatedMeasure ( 0, measure.deviceId, measure.metric, measure.timestamp, measure.value ) )
    .groupByKey
    .windowedBy ( TimeWindows.of ( Duration.ofSeconds (30 ) ).grace ( Duration.ofSeconds (30 ) ) ) // TODO 3600 / 90
    .aggregate ( AggregatedMeasure.instance () ) ( ( key, measure, accum ) => new AggregatedMeasure ( accum.count + 1, measure.deviceId, measure.metric, max ( measure.timestamp, accum.timestamp ), measure.value + accum.value ) )
    .toStream
    .map ( ( key, measure )  => ( key, new Measure ( measure.deviceId, measure.metric, ZonedDateTime.ofInstant ( key.window ().endTime (), measure.timestamp.getZone ), measure.value / measure.count ) ) ) // TOOD VERIFY THE DIVISION
    .peek ( ( key, value ) => println ( s"AGGREGATION HOURLY KEY: ${key}, VALUE: ${value}" ) ) // TODO TMP

  hourlyAggregated
    .to (  config.getString ( ConfigurationKeys.METRICS_AGGREGATION_HOURLY.key ) )

  val dailyAggregated = builder.stream [String, Measure] ( config.getString ( ConfigurationKeys.METRICS_AGGREGATION_HOURLY.key ) )
    .mapValues ( measure => new AggregatedMeasure ( 0, measure.deviceId, measure.metric, measure.timestamp, measure.value ) )
    .groupByKey
    .windowedBy ( TimeWindows.of ( Duration.ofSeconds (60 ) ).grace ( Duration.ofSeconds (30 ) ) ) // Duration.ofHours ( 24 ) / 90
    .aggregate ( AggregatedMeasure.instance () ) ( (key, measure, accum ) => new AggregatedMeasure ( accum.count + 1, measure.deviceId, measure.metric, max ( measure.timestamp, accum.timestamp ), measure.value + accum.value ) )
    .toStream
    .map ( ( key, measure )  => ( key, new Measure ( measure.deviceId, measure.metric, ZonedDateTime.ofInstant ( key.window ().endTime (), measure.timestamp.getZone ), measure.value / measure.count ) ) ) // TOOD VERIFY THE DIVISION
    .peek ( ( key, value ) => println ( s"AGGREGATION DAILY KEY: ${key}, VALUE: ${value}" ) ) // TODO TMP

  dailyAggregated
    .to ( config.getString ( ConfigurationKeys.METRICS_AGGREGATION_DAILY.key ) )


  val topology: Topology = builder.build ()

  logger.info ( topology.describe ().toString )

  val application: KafkaStreams = new KafkaStreams ( topology, props )
  application.start ()

  logger.info ( "Setting up a shutdown hook" )

  sys.ShutdownHookThread {

    application.close ( Duration.ofSeconds (10 ) )
  }

  private def max ( date1: ZonedDateTime, date2: ZonedDateTime ): ZonedDateTime = {

    import java.time.Instant

    val epoch = math.max ( date1.toEpochSecond * 1000, date2.toEpochSecond * 1000 )

    ZonedDateTime.ofInstant ( Instant.ofEpochMilli ( epoch ), date1.getZone ) // TODO VERIFY
  }
}