package jp.o3co.config

import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

object GlobalSettings extends Settings {

  override val config = ConfigFactory.load
    .withFallback(ConfigFactory.defaultReference)

  object Defaults {
    def timeout = 
      if(config.hasPath("config.default-timeout")) FiniteDuration(config.getDuration("config.default-timeout", MILLISECONDS), MILLISECONDS)
      else 1.seconds
  }
}
