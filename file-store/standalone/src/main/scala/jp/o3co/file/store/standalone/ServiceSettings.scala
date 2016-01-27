package jp.o3co.file.store
package standalone

import com.typesafe.config.Config
import jp.o3co.file.store
import jp.o3co.file.store.meta
//import jp.o3co.config._
import jp.o3co.config.{ServiceSettings => BaseServiceSettings}

trait ServiceSettings extends BaseServiceSettings with rest.ServiceSettings with meta.rest.ServiceSettings {
  override def prefix: Option[String] = config.getOption[String]("prefix")
}

object ServiceSettings {
  def apply(defaultConfig: Config) = new ServiceSettings {
    override lazy val config = defaultConfig
  }
}
