package jp.o3co.file.store
package meta 

import com.typesafe.config.Config
import akka.actor.{Actor, ActorRef, ActorLogging}
import akka.pattern.ask
import akka.pattern.pipe
import akka.util.Timeout
import dal.DALActor

/**
 * 
 *
 */
class LocalServiceActor(config: Config) extends Actor with ActorLogging {

  /**
   * Initiali settings from configuration
   */
  val settings = LocalServiceSettings(config) 

  val endpoint: ActorRef = context.actorOf(DALActor.props(settings.dal), "dal")

  val generator: ActorRef = ids.IDGeneratorActor(settings.ids)

  implicit val executionContext = context.dispatcher

  implicit val timeout: Timeout = settings.timeout

  def receive: Receive = receiveEvent 

  def receiveEvent: Receive  = {
    case newMeta: RegisterMeta => 
      (generator ? ids.Generate)
        .flatMap {
          case ids.GenerateResult(id) => 
            val meta = newMeta.withId(id)
            (endpoint ? dal.PutEntity(meta))
              .map {
                case dal.PutEntityResult() => RegisterMetaSuccess(meta) 
              }
              .recover {
                case e => RegisterMetaFailure(new Exception("Failed to put entity", e))
              }
        } pipeTo sender
    case event if (sender != endpoint) => 
      endpoint.tell(event, sender)
      log.info(s"Forward event ${event} to endpoint ${endpoint.path}.")
  }
}

