package mdaros.labs.kafka.aggregator.serde

import org.apache.kafka.common.serialization.Serializer

import java.io.{ ByteArrayOutputStream, ObjectOutputStream }
import java.util.Map

class TypedSerializer [T] extends Serializer [T] {

  override def configure ( configs: Map [ String, _ ], isKey: Boolean ): Unit = {

  }

  override def serialize ( topic: String, data: T ): Array [ Byte ] = {

    try {

      val byteOut = new ByteArrayOutputStream ()
      val objOut = new ObjectOutputStream ( byteOut )
      objOut.writeObject ( data )
      objOut.close ()
      byteOut.close ()

      byteOut.toByteArray
    }
    catch {

      case ex: Exception => throw new Exception ( ex.getMessage )
    }
  }

  override def close (): Unit = {

  }
}