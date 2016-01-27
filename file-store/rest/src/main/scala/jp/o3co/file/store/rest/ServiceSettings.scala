package jp.o3co.file.store.rest

import akka.actor.ActorRefFactory
import com.typesafe.config.Config
import java.net.URI
import jp.o3co.file.store.util.URIMatchers
import jp.o3co.file.store.util.{URITransformer, URITransformers, MatchedURITransformer}
import scala.collection.JavaConversions._
import spray.http.{MediaRange, MediaRanges}

import jp.o3co.config._
import jp.o3co.config.{ServiceSettings => BaseServiceSettings}

/**
 *
 */
trait ServiceSettings extends BaseServiceSettings {

  /**
   * Get the default download mode from configuration
   */
  def downloadMode: DownloadMode =  DownloadMode(config.getOrElse("download-mode", "redirect"))

  def acceptTypes: Seq[MediaRange] = {
    val types = config.getOrElse("accept-types", Seq())

    types.map(t => MediaRanges.getForKey(t))

    if(types.isEmpty) Seq(MediaRanges.`*/*`)
    else types.map(t => MediaRanges.getForKey(t)).collect {
        case Some(r) => r
      }
  }

  def prefix: Option[String] = config.getOption[String]("prefix")

  val storePath: String = config.getOption[String]("store_path")
    .getOrElse(throw new Exception("""Config "store_path" is not specified."""))

  //val store: ActorPath = ActorPath.fromString(storePath)
  def store(implicit actorRefFactory: ActorRefFactory) = actorRefFactory.actorSelection(storePath)

  def isDownloadEnabled: Boolean  = config.getOrElse("enable_download", true)

  def isUploadEnabled: Boolean    = config.getOrElse("enable_upload", true)
}

object ServiceSettings {
  def apply(c: Config) = new ServiceSettings {
    override lazy val config = c
  }
}
