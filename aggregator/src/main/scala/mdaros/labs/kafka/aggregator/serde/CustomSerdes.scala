package mdaros.labs.kafka.aggregator.serde

import mdaros.labs.kafka.aggregator.AggregatedMeasure
import mdaros.labs.kafka.model.Measure

object CustomSerdes {

  class MeasureSerializer extends TypedSerializer [Measure]
  class MeasureDeserializer extends TypedDeserializer [Measure]
  class MeasureSerde extends TypedSerde [Measure]

  class AggregatedMeasureSerializer extends TypedSerializer [AggregatedMeasure]
  class AggregatedMeasureDeserializer extends TypedDeserializer [AggregatedMeasure]
  class AggregatedMeasureSerde extends TypedSerde [AggregatedMeasure]
}