import java.util.Properties

import config.Config
import kafka.NewsSerializer
import models.News
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer

import scala.io.Source
import scala.xml.XML

object Producer extends App with Config {

  def read(file: String) = {
    val source = Source.fromFile(file)
    val lines = source.getLines().mkString("\n")
    source.close()

    val xml = XML.loadString(lines)
    val data = xml \ "DOC"
    data.map(e => {
      val id = (e \\ "@id").mkString("")
      val docType = (e \\ "@type").mkString("")
      val headLine = (e \\ "HEADLINE").text.mkString("")
      val dateLine = (e \\ "DATELINE").text.mkString("")
      val text = e \\ "TEXT"
      val temp = (text \\ "P").map(p => {
        p.text.mkString("")
      })

      val sentences = if (temp.isEmpty) Seq(text.text.mkString("")) else temp
      News(id, docType, headLine, dateLine, sentences)
    })
  }

  val news = read(inputFilePath)
  val props = new Properties()
  props.put("bootstrap.servers", brokers)
  props.put("client.id", "NewsProducer")
  props.put("key.serializer", classOf[StringSerializer])
  props.put("value.serializer", classOf[NewsSerializer])

  val producer = new KafkaProducer[String, News](props)
  news.foreach { each =>
    val data = new ProducerRecord[String, News](topic, each.docType, each)
    producer.send(data)
  }

  producer.close()
}
