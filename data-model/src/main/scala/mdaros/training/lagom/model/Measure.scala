package mdaros.training.lagom.model

import java.time.ZonedDateTime

case class Measure ( deviceId: String, metric: String, timestamp: ZonedDateTime, value: Double )