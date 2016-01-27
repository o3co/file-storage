package jp.o3co.file.store
package storage
package local

import com.typesafe.config.Config
import java.net.URI

case class LocalStorageSettings(override val config: Config) extends StorageSettings {

  def getBaseUri: URI = new URI(config.getString("base_dir"))

}
