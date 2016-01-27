package jp.o3co.file.store

import akka.actor.Actor
import akka.pattern.pipe
import com.typesafe.config.Config

/**
 * 
 */
class LocalStoreActor(config: Config) extends Actor 
  with impl.StoreImpl 
{
  implicit override lazy val actorRefFactory = context

  override lazy val settings = LocalStoreSettings(config)

  implicit val executionContext = context.dispatcher

  def receive = {
    case GetResource(id)  => 
      getResource(id)
        .map { resource => 
          GetResourceSuccess(resource) 
        }
        .recover {
          case ex => GetResourceFailure(ex)
        } pipeTo sender
    case GetLink(id) =>
      getLink(id)
        .map { link => 
          GetLinkSuccess(link)
        }
        .recover {
          case ex => GetLinkFailure(ex)
        } pipeTo sender
    case PutResource(resource, segment, contentType, name) => 
      putResource(resource, segment, contentType, name)
        .map { meta => 
          PutResourceSuccess(meta)
        }
        .recover {
          case ex => PutResourceFailure(ex)
        } pipeTo sender
  }
}

