package jp.o3co.file.store

import com.typesafe.config.Config
import jp.o3co.file.store.naming._
import akka.actor.{ActorSelection, ActorRefFactory}
import jp.o3co.file.store.storage.FileStorages
import java.net.URI

trait Segment extends Implicits {
  def storagePluginId: String

  def storage: ActorSelection

  def namingPattern: String

  def naming: Naming
}

object Segment {
  /**
   *
   */
  def apply(name: SegmentName)   = SegmentAlias(name)

  /**
   *
   */
  def apply(config: Config)(implicit actorRefFactory: ActorRefFactory) = SegmentDefinition(config)

  def path(segment: SegmentName, path: RelativePath): URI = new URI("resource://%s/%s".format(segment.name, path))
}

/**
 *
 */
case class SegmentAlias(name: SegmentName) extends Segment {

  def storagePluginId: String = throw new Exception("SegmentAlias cannot provide storagePluginId")

  def storage: ActorSelection = throw new Exception("SegmentAlias cannot provide store")

  def namingPattern: String = throw new Exception("SegmentAlias cannot provide namingPattern")

  def naming: Naming = throw new Exception("SegmentAlias cannot provide naming")
}

/**
 *
 */
case class SegmentDefinition(config: Config)(implicit val actorRefFactory: ActorRefFactory) extends Segment{

  /**
   *
   */
  lazy val storagePluginId: String = config.getString("storage-plugin")

  /**
   * Get SegmentStorage
   */
  def storage: ActorSelection = FileStorages(storagePluginId)

  /**
   *
   */
  lazy val namingPattern: String = config.getString("naming")

  /**
   *
   */
  lazy val naming: Naming = PatternNaming(namingPattern)
}
