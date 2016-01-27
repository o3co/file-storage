package jp.o3co.file.store
package meta 
package dal

import akka.actor.{Actor, Props}
import com.typesafe.config.Config


/**
 *
 */
class MySQLActor(override val settings: MySQLSettings) extends SlickDALActor with impl.MySQLImpl {
}

object MySQLActor {
  /**
   *
   */
  def props(settings: MySQLSettings) = Props(classOf[MySQLActor], settings)
}
