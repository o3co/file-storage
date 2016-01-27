package jp.o3co.file
package store

import akka.actor.{ActorRefFactory, ActorRef, Props}
import jp.o3co.domain.config.Settings
import com.typesafe.config.Config

case class RootStoreSettings(config: Config) extends Settings {

  def endpoint(implicit actorRefFactory: ActorRefFactory): ActorRef = 
    if(config.hasPath("path")) actorRefFactory.actorOf(Props(classOf[RemoteStoreActor], config), "endpoint")
    else actorRefFactory.actorOf(Props(classOf[LocalStoreActor], config), "endpoint")
}
