package mongo

import com.google.gson.Gson
import config.{ActorsConfig, Config}
import models.{MongoEntry, ParsedSentence}
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}

import scala.util.{Failure, Success}

trait Mongo extends ActorsConfig {

  def insertRecords(col: MongoCollection[Document], records: Seq[ParsedSentence]) = {
    val gson = new Gson()
    val rec = records.map(r => Document(gson.toJson(r)))
    col.insertMany(rec).toFuture().onComplete {
      case Success(_) => println("Insertion Complete")
      case Failure(ex) => println(ex.getMessage)
    }
  }

  def readRecords(col: MongoCollection[Document]) = {
    val gson = new Gson()
    col.find().toFuture().onComplete {
      case Success(r) => r.map(e => gson.toJson(e)).map(e => gson.fromJson(e, classOf[MongoEntry])).foreach(println)
      case Failure(ex) => println(ex.toString)
    }
  }
}

object Mongo extends Config with ActorsConfig {

  def loadCollection = {
    val uri = s"mongodb://$host:$port"
    val mongoClient = MongoClient(uri)
    val mongoDb : MongoDatabase = mongoClient.getDatabase(database)
    mongoDb.getCollection(collection)
  }
}
