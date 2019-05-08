import com.google.gson.Gson
import com.mongodb.spark.MongoSpark
import com.mongodb.spark.config.ReadConfig
import org.apache.spark.{SparkConf, SparkContext}
import config.{ActorsConfig, Config}
import models.{OptionalMongoEntry, OptionalParsedSentence}
import xml.CreateXml
import spray.json._

object MongoToXml extends App with ActorsConfig with CreateXml with Config with DefaultJsonProtocol {

  val sparkConf = new SparkConf().setMaster("local[*]").setAppName("test")
  sparkConf.set("spark.mongodb.input.uri", s"$mongoUrl")
  val sparkContext = new SparkContext(sparkConf)

  val readConfig = ReadConfig(Map("collection" -> collection), Some(ReadConfig(sparkContext)))

  val gson = new Gson()
  val customRdd = MongoSpark.load(sparkContext, readConfig)

  implicit val parsedSentenceFormat: RootJsonFormat[OptionalParsedSentence] = jsonFormat8(OptionalParsedSentence)
  implicit val mongoEntryFormat: RootJsonFormat[OptionalMongoEntry] = jsonFormat6(OptionalMongoEntry)

  val jsonVersion = customRdd.map(_.toJson)
  val sentences = jsonVersion.collect().map(e => e.parseJson.convertTo[OptionalMongoEntry])

  generateXml(sentences, outputFilePath)
  sparkContext.stop()
}