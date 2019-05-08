package config

import com.typesafe.config.ConfigFactory

trait Config {

  val config: com.typesafe.config.Config = ConfigFactory.load(
    s"${System.getProperty("env","reference")}.conf"
  )

  val kafka: com.typesafe.config.Config = config.getConfig("kafka")
  val brokers: String = kafka.getString("brokers")
  val topic: String = kafka.getString("topic")
  val consumerGroup: String = kafka.getString("groupId")
  val readCriteria: String = kafka.getString("read-criteria")
  val offset: Int = System.getProperty("offset", "0").toInt

  val checkpointDir: String = config.getString("checkpoint")
  val test: String = config.getString("test-sentence")

  val db: com.typesafe.config.Config = config.getConfig("db")
  val mongo: com.typesafe.config.Config = db.getConfig("mongo")
  val host: String = mongo.getString("host")
  val database: String = mongo.getString("database")
  val collection: String = mongo.getString("collection")
  val port: Int = mongo.getInt("port")
  val username: String = mongo.getString("username")
  val password: String = mongo.getString("password")
  val mongoUrl: String = username.isEmpty match {
    case true => s"mongodb://$host/$database.$collection"
    case false => s"mongodb+srv://$username:$password@$host/$database.$collection"
  }

  val inputFilePath: String = System.getProperty("input", "")
  val outputFilePath: String = System.getProperty("output", "")
}
