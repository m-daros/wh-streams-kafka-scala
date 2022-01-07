package mdaros.labs.kafka.model

import java.time.ZonedDateTime

case class Measure ( deviceId: String, metric: String, timestamp: ZonedDateTime, value: Double )

object Measure {

  private def instance (): Measure = {

    new Measure ( "", "", ZonedDateTime.now (), 0 )
  }
}