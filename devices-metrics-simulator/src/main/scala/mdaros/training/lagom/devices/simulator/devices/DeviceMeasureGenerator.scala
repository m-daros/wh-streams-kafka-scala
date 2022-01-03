package mdaros.training.lagom.devices.simulator.devices

import akka.stream.alpakka.mqtt.MqttMessage
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule

import com.typesafe.config.Config
import mdaros.training.lagom.devices.simulator.config.ConfigurationKeys.{ CLIENT_ID_PREFIX, TOPIC }
import mdaros.training.lagom.model.Measure

import java.time.ZonedDateTime
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class DeviceMeasureGenerator {

  val jsonMapper = new ObjectMapper ()
  jsonMapper.registerModule ( DefaultScalaModule )

  jsonMapper.registerModule ( new JavaTimeModule )
  jsonMapper.disable ( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS )

  private val randomGenerator = new scala.util.Random

  def generateMeasures ( index: Int, config: Config ): Source [ MqttMessage, Any ] = {

    val topic          = config.getString ( TOPIC.key )
    val clientIdPrefix = config.getString ( CLIENT_ID_PREFIX.key )

    val clientId = s"${clientIdPrefix}${index}"

    Source.tick ( 0 seconds, 5 seconds, ZonedDateTime.now () )
      .map ( timestamp => Seq ( Measure ( clientId, "cpu.usage", timestamp, randomGenerator.nextDouble () * 100 ),
                                Measure ( clientId, "mem.usage", timestamp, randomGenerator.nextDouble () * 100 ),
                                Measure ( clientId, "disk.usage", timestamp, randomGenerator.nextDouble () * 100 ) ) )
      .map ( measure => MqttMessage ( topic, ByteString ( jsonMapper.writer ().writeValueAsString ( measure ) ) ) )
  }
}