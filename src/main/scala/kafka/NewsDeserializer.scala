package kafka

import java.io.{ByteArrayInputStream, ObjectInputStream}
import java.util

import models.News
import org.apache.kafka.common.serialization.Deserializer

class NewsDeserializer extends Deserializer[News] {

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def deserialize(topic: String, data: Array[Byte]): News = {
    val byteIn = new ByteArrayInputStream(data)
    val objIn = new ObjectInputStream(byteIn)
    val obj = objIn.readObject().asInstanceOf[News]
    byteIn.close()
    objIn.close()
    obj
  }

  override def close(): Unit = {}

}
