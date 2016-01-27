package jp.o3co.file
package storage
package local

import org.apache.commons.io.FileUtils
import scala.concurrent.Future
import com.typesafe.config.Config
import java.io.File

/**
 *
 */
class S3FileStorage(config: Config) extends FileStorage {
  
  val settings = Settings(config)

  def bucket: Bucket = client.getBucket(settings.bucket)

  def getObject(key: String): S3Object = bucket.getObject(key)

  /**
   *
   */
  def loadResource(path: Path) = Future {
    // Copy to local temp reosurce
    val obj = getObject(path.toString)
    TemporaryFileResource(obj.toBytes)
  }

  /**
   *
   */
  def saveResource(path: Path, resource: Resource) = Future {
    resource.read { stream => 
      bucket.putObject(path.toString, stream, new ObjectMetadata())
    }
  }
}

