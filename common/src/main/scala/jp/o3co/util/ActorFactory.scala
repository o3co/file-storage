package jp.o3co.util

import jp.o3co.config._
import com.typesafe.config.Config
import akka.actor.{ActorRef, ActorRefFactory, Props}

trait ActorFactory {
  def apply(config: Config, args: Any*)(name: String = null)(implicit actorRefFactory: ActorRefFactory): ActorRef = {
    val classname = config.getOrElse[String]("class", throw new Exception(s"""Config "class" is not specified."""))

    val props = Props(Class.forName(classname), Seq(config) ++ args: _*)

    Option(name) match {
      case Some(n) => actorRefFactory.actorOf(props, n)
      case None    => actorRefFactory.actorOf(props)
    }
  }
}

object ActorFactory extends ActorFactory

