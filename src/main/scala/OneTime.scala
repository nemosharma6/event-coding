import java.util.Properties

import com.google.gson.Gson
import com.mongodb.spark.MongoSpark
import config.{ActorsConfig, Config}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.kafka010.{KafkaUtils, OffsetRange}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import edu.stanford.nlp.pipeline.{CoreDocument, StanfordCoreNLP}
import kafka.NewsDeserializer
import models.{MongoEntry, News, ParsedSentence}
import org.bson.Document
import simhash.SimHash

object OneTime extends App with Config with ActorsConfig with SimHash {

  val kafkaParams = Map[String, Object](
    "bootstrap.servers" -> brokers,
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[NewsDeserializer],
    "group.id" -> consumerGroup,
    "auto.offset.reset" -> readCriteria,
    "enable.auto.commit" -> (false: java.lang.Boolean)
  ).asJava

  val sparkConf = new SparkConf().setMaster("local[*]").setAppName("one-time")
  sparkConf.set("spark.mongodb.output.uri", mongoUrl)

  val sparkContext = new SparkContext(sparkConf)
  val offsetRanges = Array(OffsetRange(topic, 0, 0, offset))

  val rdd = KafkaUtils.createRDD[String, News](sparkContext, kafkaParams, offsetRanges, PreferConsistent)
    .map(_.value())

  rdd.cache()

  val hash = rdd.map(e => (simHash(e.sentences.mkString("").trim), e))
  val possibleMatches = hash.flatMap {
    case (h, n) =>
      (0 to 31).map { ind =>
        (h.patch(ind, Seq(1 - h(ind)), 1), (h, n))
      }
  }

  val newHash = hash.map(e => (e._1.mkString(","), e._2))
  val newPossibleMatches = possibleMatches.map(e => (e._1.mkString(""), (e._2._1.mkString(""), e._2._2)))
  val unique = newHash.groupBy(_._1).map(_._2.head)
  val check = newPossibleMatches.join(unique).groupBy(_._1).map {
    case (h, ns) => (h, ns.head._2._2)
  }

  val postDedupSet = unique.union(check)

  val props = new Properties()
  props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,depparse,coref,kbp,quote")
  props.setProperty("coref.algorithm", "neural")

  val pipeline = new StanfordCoreNLP(props)

  val mongoEntries = postDedupSet.map {
    case (h, n) =>
      val parsedSentences =
        n.sentences.map { s =>
          val doc = new CoreDocument(s)
          pipeline.annotate(doc)

          ParsedSentence(
            s.hashCode.toString,
            doc.sentences().get(0).text(),
            doc.sentences().get(0).constituencyParse().toString,
            doc.sentences().get(0).dependencyParse().toCompactString,
            doc.tokens().map(_.originalText()).toArray,
            doc.tokens().map(_.lemma()).toArray,
            doc.tokens().map(_.ner()).toArray,
            doc.corefChains().map(_._2.toString).toArray
          )
        }.toArray

      MongoEntry.create(h, n, parsedSentences)
  }

  val gson = new Gson()
  val docs = mongoEntries.map(e => Document.parse(gson.toJson(e)))
  MongoSpark.save(docs)

  sparkContext.stop()
}
