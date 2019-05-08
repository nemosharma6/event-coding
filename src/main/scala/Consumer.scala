import config.{ActorsConfig, Config}
import kafka.NewsDeserializer
import models.News
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Minutes, Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe

object Consumer extends App with Config with ActorsConfig {

  val kafkaParams = Map[String, Object](
    "bootstrap.servers" -> brokers,
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[NewsDeserializer],
    "group.id" -> consumerGroup,
    "auto.offset.reset" -> readCriteria,
    "enable.auto.commit" -> (false: java.lang.Boolean)
  )

  val sparkConf = new SparkConf().setAppName("consumer").setMaster("local[*]")

  val topics = Array(topic)
  val streamingContext = new StreamingContext(sparkConf, Seconds(10))

  val stream = KafkaUtils.createDirectStream[String, News](
    streamingContext,
    PreferConsistent,
    Subscribe[String, News](topics, kafkaParams)
  )

  val values = stream.map(_.value())
  values.print()

  streamingContext.remember(Minutes(1))
  streamingContext.checkpoint(checkpointDir)

  streamingContext.start()
  streamingContext.awaitTerminationOrTimeout(5000)
}
