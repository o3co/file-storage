package jp.o3co.file.store
package meta 
package dal

import akka.actor.{Actor, Props}
import com.typesafe.config.Config

/**
 *
 */
class H2Actor(override val settings: BaseH2Settings) extends SlickDALActor with impl.H2Impl {

  //override def receive = receiveEvent
}

object H2Actor {
  /**
   *
   */
  def props(settings: BaseH2Settings) = Props(classOf[H2Actor], settings)
}
