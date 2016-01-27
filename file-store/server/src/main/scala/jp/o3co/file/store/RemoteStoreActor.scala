package jp.o3co.file.store

import akka.actor.Actor
import akka.pattern.pipe
import com.typesafe.config.Config

/**
 * 
 */
class RemoteStoreActor(config: Config) extends Actor 
{
  implicit val actorRefFactory = context

  val settings = RemoteStoreSettings(config)

  implicit val executionContext = context.dispatcher

  val endpoint = settings.endpoint

  def receive = {
    case event if sender != endpoint => 
      endpoint.tell(event, sender)
  }
}


