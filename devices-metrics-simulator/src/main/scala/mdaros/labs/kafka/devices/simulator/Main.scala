package mdaros.labs.kafka.devices.simulator

import com.softwaremill.macwire.wire

object Main extends App {

  wire [ DeviceMetricsSimulator ].run ()
}