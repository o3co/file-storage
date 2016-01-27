package jp.o3co.domain

import scala.collection.JavaConversions._
import com.typesafe.config.{Config, ConfigFactory, ConfigValueType}

package object config {
  
  /**
   *
   */
  implicit class ServiceConfig(val underlying: Config) extends AnyVal {

    /**
     * enalbed is default true 
     */
    def isServiceEnabled: Boolean = !underlying.hasPath("enabled") || underlying.getBoolean("enabled")
    
    def isBootable: Boolean = 
      (underlying.hasPath("class"), isServiceEnabled) match {
        case (true, true) => true
        case _ => false
      }

    /**
     * Get Class with "class" path configured value
     */
    def serviceClass = Class.forName(underlying.getString("class"))

    /**
     * Get the service path of the child
     *
     * {{{
     *   val endpoint: String = config.serviceRef("endpoint") 
     * }}}
     * will grab the configuration as following
     * {{{
     *  parent {
     *    child {
     *      // service configuration
     *      path = "/actor/path"
     *    }
     *    or 
     *    chlid = "/actor/path"
     *  }
     * }}}
     *
     *
     */
    def serviceRef(path: String): String = {
      val configValue = underlying.getValue(path)
      configValue.valueType() match {
        case ConfigValueType.OBJECT => underlying.getConfig(path).getString("path")
        case _ => configValue.unwrapped().toString
      }
    }

    def serviceRefConfig(path: String): Config = {
      val configValue = underlying.getValue(path)
      configValue.valueType() match {
        case ConfigValueType.OBJECT => underlying.getConfig(path)
        case _ => ConfigFactory.parseMap(Map("path" -> configValue.unwrapped()))
      }
    }
  }
}
