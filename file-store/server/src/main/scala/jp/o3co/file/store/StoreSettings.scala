package jp.o3co.file
package store

import akka.actor.{ActorRefFactory, ActorSelection}
import com.typesafe.config.Config
import jp.o3co.domain.config.Settings

/**
 * StoreSettings
 */
case class LocalStoreSettings(config: Config) extends Settings {
  
  /**
   * Get ActorSelection from the configuration
   */
  def meta: Config = config.getConfig("meta")

  /**
   * Get 
   *
   */
  def segments: Config = config.getConfig("segments")
}

case class RemoteStoreSettings(config: Config) extends Settings {
  /**
   *
   */
  def endpoint(implicit actorRefFactory: ActorRefFactory): ActorSelection = {
    actorRefFactory.actorSelection(config.getOrElse[String]("path", throw new Exception("""Configure for "path" is required"""))) 
  }
}
