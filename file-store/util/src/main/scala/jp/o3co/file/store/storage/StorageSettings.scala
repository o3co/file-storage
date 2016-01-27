package jp.o3co.file.store
package storage

import com.typesafe.config.Config
import java.net.URL

/**
 *
 */
trait StorageSettings {
  def config: Config

  lazy val isPublicEnabled: Boolean = config.hasPath("public_path") 

  lazy val publicPath: URL = 
    new URL(config.getString("public_url"))
}
