package jp.o3co.file.store
package meta

import akka.actor.{Actor, ActorRef, ActorRefFactory, ActorSelection, Props}
import com.typesafe.config.Config

object ServiceAdapter {
  def apply(config: Config, name: String = null)(implicit actorRefFactory: ActorRefFactory): ActorRef = {
    val nameOption = Option(name)
    val props = Props(classOf[ServiceAdapterActor], config)

    if(nameOption.isDefined) actorRefFactory.actorOf(props, nameOption.get)
    else actorRefFactory.actorOf(props)
  }
}

class ServiceAdapterActor(config: Config) extends Actor {
  
  val endpoint: ActorSelection = context.actorSelection(config.getString("endpoint"))
  def receive: Receive = {
    case event if sender != endpoint => 
      endpoint.tell(event, sender)
  }
}
