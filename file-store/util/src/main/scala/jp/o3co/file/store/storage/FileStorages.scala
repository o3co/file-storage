package jp.o3co.file.store
package storage

import com.typesafe.config.Config
import akka.actor.{ActorPath, ActorSelection, ActorRefFactory}
import jp.o3co.config.GlobalSettings
import scala.collection.mutable

/**
 * Singleton manage to provide single FileStorage actor for each storage plugin
 */
object FileStorages {
  /*
   * ID vs ActorPath
   */
  val paths: mutable.Map[String, ActorPath] = mutable.Map()

  def boot(segments: Segments, baseConfig: Config = null)(implicit actorRefFactory: ActorRefFactory): Unit = {
    // If config is not specified, then load default root config
    val config = Option(baseConfig).getOrElse(GlobalSettings.config)
    segments.values.foreach { segment => 
      // Register only new storage
      if(!paths.contains(segment.storagePluginId)) {
        // Boot the storage
        paths.put(segment.storagePluginId, FileStorage(config.getConfig(segment.storagePluginId)).path)
      }
    }
  }

  /**
   * Boot Actor or get ActorSelection of booted
   */
  def apply(id: String)(implicit actorRefFactory: ActorRefFactory): ActorSelection = {
    if(actorRefFactory == null) throw new NullPointerException("actorRefFactory cannot be null")
    actorRefFactory.actorSelection(
      paths.get(id).getOrElse(throw new Exception(s"Storage $id is not booted."))
    )
  }
}
