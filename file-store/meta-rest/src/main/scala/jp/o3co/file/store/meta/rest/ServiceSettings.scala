package jp.o3co.file.store.meta.rest

import akka.actor.{ActorPath, ActorRefFactory}
import com.typesafe.config.Config
import jp.o3co.config._
import jp.o3co.config.{ServiceSettings => BaseServiceSettings}

trait ServiceSettings extends BaseServiceSettings {

  def prefix: Option[String] = config.getOption[String]("prefix")

  def metaPath: String = config.getOption[String]("meta_path")
    .getOrElse("""Config "meta_path" is not specified.""")

  //def meta: ActorPath = ActorPath.fromString(metaPath.getOrElse(throw new Exception(s"""Config "meta_path" is not specified.""")))
  def meta(implicit actorRefFactory: ActorRefFactory) = actorRefFactory.actorSelection(metaPath)
}

object ServiceSettings {
  def apply(c: Config) = new ServiceSettings {
    override lazy val config = c
  }
}

