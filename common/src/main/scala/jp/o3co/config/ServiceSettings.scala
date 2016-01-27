package jp.o3co.config

import scala.collection.JavaConversions._
import com.typesafe.config.{Config, ConfigFactory, ConfigValueType}

/**
 * Domain service settings
 */
trait ServiceSettings extends Settings with TimeoutSupport {
  
}
