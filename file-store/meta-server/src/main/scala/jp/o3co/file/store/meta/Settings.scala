package jp.o3co.file.store
package meta 

import com.typesafe.config.Config
import jp.o3co.config._


/**
 * @param config 
 */
case class LocalServiceSettings(val config: Config) extends ServiceSettings {

  /**
   * Get dal configuration
   */
  def dal = config.getConfig("dal")

  def ids = config.getConfig("id")
}
