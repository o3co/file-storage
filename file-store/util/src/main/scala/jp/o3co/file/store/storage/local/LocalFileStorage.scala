package jp.o3co.file.store
package storage
package local

import com.typesafe.config.Config
import java.io.File
import java.net.URL
import org.apache.commons.io.FileUtils
import scala.concurrent.Future
import akka.actor.ActorLogging

/**
 *
 */
class LocalFileStorage(config: Config) extends FileStorage with ActorLogging {
  
  val settings = LocalStorageSettings(config)

  /**
   * Resolve the actual filepath
   */
  protected def getFile(path: RelativePath): File = 
    try {
      new File(settings.getBaseUri.resolve(path.toString))
    } catch {
      case e: Throwable => throw new Exception(s"Fialed to get File ${path}", e)
    }

  def resolveLink(path: RelativePath) = Future {
    if(settings.isPublicEnabled) Option(Link(new URL(settings.publicPath, path)))
    else None
  }

  /**
   *
   */
  def loadResource(path: RelativePath) = Future {
    try {
      Option(LocalFileResource(getFile(path)))
    } catch {
      case e: java.io.FileNotFoundException => None
    }
  }

  /**
   *
   */
  def saveResource(resource: Resource, path: RelativePath) = {
    val file = getFile(path)
    resource.read { stream => 
      FileUtils.copyInputStreamToFile(stream, file)
    }

    log.info(s"""Resource is saved on "$file"""")
    
    // 
    resolveLink(path)
  }
}
