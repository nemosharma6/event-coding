package config

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext

trait ActorsConfig extends Config {
  implicit val system: ActorSystem = ActorSystem("deduplication", config)
  implicit val executor: ExecutionContext = system.dispatcher
  implicit val actorMaterializer: ActorMaterializer = ActorMaterializer()
}
