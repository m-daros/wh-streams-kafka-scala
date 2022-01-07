package mdaros.labs.kafka.devices.simulator.config

import com.typesafe.config.{ Config, ConfigFactory }
import Configurator.SERVICE_NAME

class Configurator {

  def getConfig (): Config = {

    ConfigFactory.load ( "application.conf" )
      .getConfig ( SERVICE_NAME )
  }
}

object Configurator {

  val SERVICE_NAME = "devices-metrics-simulator"
}