package jp.o3co.file.store

import akka.actor.{Actor, ActorRef}
import akka.pattern.pipe
import com.typesafe.config.Config

/**
 * 
 */
class RootStoreActor(config: Config) extends Actor 
{
  implicit val actorRefFactory = context

  implicit val executionContext = context.dispatcher

  val settings = RootStoreSettings(config)

  // Get endpoint of the store
  val endpoint: ActorRef = settings.endpoint

  def receive = {
    case event if sender != endpoint => 
      endpoint.tell(event, sender)
  }
}

