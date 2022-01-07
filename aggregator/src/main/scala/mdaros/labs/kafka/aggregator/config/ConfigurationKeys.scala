package mdaros.labs.kafka.aggregator.config

object ConfigurationKeys extends Enumeration {

  protected case class ConfigurationKey ( key: String ) extends super.Val

  val APPLICATION_ID              = ConfigurationKey ( "application-id" )
  val BOOTSTRAP_SERVERS           = ConfigurationKey ( "bootstrap-servers" )
  val METRICS                     = ConfigurationKey ( "input-topic" )
  val METRICS_AGGREGATION_HOURLY  = ConfigurationKey ( "metrics-aggregation-hourly-topic" )
  val METRICS_AGGREGATION_DAILY   = ConfigurationKey ( "metrics-aggregation-daily-topic" )
}