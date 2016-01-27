package jp.o3co.file.store
package meta
package ids

import akka.actor.{Actor, ActorRef, ActorSelection, ActorRefFactory, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.Config
import java.util.UUID
import jp.o3co.config._
import jp.o3co.util.generator.RandomUUIDGenerator
import scala.concurrent.{Await, ExecutionContext}
import jp.o3co.file.store.meta.dal.{ContainsEntity, ContainsEntityResult}

object IDGeneratorActor {
  def apply(config: Config)(implicit actorRefFactory: ActorRefFactory): ActorRef = {
    val settings = RootSettings(config)

    if(settings.isRemote) actorRefFactory.actorOf(Props(classOf[RemoteIDGeneratorActor], config), "id")
    else  actorRefFactory.actorOf(Props(classOf[LocalIDGeneratorActor], config), "id")
  }
}

/**
 *
 */
class LocalIDGeneratorActor(config: Config) extends Actor with RandomUUIDGenerator {

  val settings = LocalSettings(config)
  /**
   *
   */
  val dal = context.actorSelection("../dal")
  
  /**
   *
   */
  implicit val executionContext: ExecutionContext = context.dispatcher 

  /**
   *
   */
  override val numOfTries = settings.retries + 1

  /**
   *
   */
  override def validation = { value: UUID => 

    implicit val timeout: Timeout = settings.validationTimeout

    val isContains = (dal ? ContainsEntity(ResourceId(value)))
      .map { 
        case ContainsEntityResult(c) => c 
      }
      .recover {
        case e => throw new Exception("Failed to validate id", e) 
      }

    !Await.result(isContains, settings.validationTimeout)
  }

  /**
   *
   */
  def receive = {
    case Generate => 
      sender ! GenerateResult(ResourceId(generate))
  }
}

/**
 *
 */
class RemoteIDGeneratorActor(config: Config) extends Actor {

  val settings = RemoteSettings(config)
  /**
   *
   */
  val endpoint: ActorSelection = settings.endpoint

  /**
   *
   */
  def receive = {
    case event if (endpoint != sender) => 
      endpoint.tell(event, sender) 
  }
}
