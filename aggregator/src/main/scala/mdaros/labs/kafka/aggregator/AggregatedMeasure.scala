package mdaros.labs.kafka.aggregator

import mdaros.labs.kafka.model.Measure

import java.time.ZonedDateTime

// TODO extends Measure
case class AggregatedMeasure ( count: Int, deviceId: String, metric: String, timestamp: ZonedDateTime, value: Double )

object AggregatedMeasure {

  def instance (): AggregatedMeasure = {

    new AggregatedMeasure ( 0, "", "", ZonedDateTime.now (), 0 )
  }
}