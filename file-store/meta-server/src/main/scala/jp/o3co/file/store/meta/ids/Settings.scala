package jp.o3co.file.store.meta.ids

import com.typesafe.config.Config
import akka.actor.{ActorSelection, ActorRefFactory}
import scala.concurrent.duration._
import jp.o3co.config._


case class RootSettings(config: Config) {
  def isRemote: Boolean = config.hasPath("path") 
}

case class LocalSettings(config: Config) {

  /**
   * 
   */
  def validationTimeout: FiniteDuration = config.get[FiniteDuration]("validation-timeout")

  def retries: Int = config.getOption[Int]("retries").getOrElse(0)
}

case class RemoteSettings(config: Config) {
  def endpoint(implicit actorRefFactory: ActorRefFactory): ActorSelection = actorRefFactory.actorSelection(config.get[String]("remote"))
}
