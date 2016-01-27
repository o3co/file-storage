package jp.o3co.config

import com.typesafe.config.{Config, ConfigFactory}

trait ApplicationSettings extends ConfigParser {

  /**
   * Load application.conf and reference.conf
   */
  def defaultConfig: Config = ConfigFactory
    .load
    .withFallback(ConfigFactory.defaultReference)

  def config: Config = defaultConfig
}
