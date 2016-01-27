package jp.o3co.config

import com.typesafe.config.{Config, ConfigFactory}
import scala.collection.JavaConversions._

trait ConfigParser {
  /**
   * Parse Map[String, Option[_]] to Config
   */
  def configMap(options: Map[String, Option[_]]): Config = {
    ConfigFactory.parseMap(options.collect {
      case (key, Some(value)) => (key, value)
    })
  }

  protected def parseLine(key: String, value: Any): String = {
    s"""${key} = ${parseValue(value)}"""
  }

  protected def parseValue(value: Any): String = {
    value match {
      case v: String         => s""""${parseScala(v)}""""
      case v: Int            => s"""${parseScala(v)}"""
      case v: Long           => s"""${parseScala(v)}"""
      case v: Double         => s"""${parseScala(v)}"""
      case v: Float          => s"""${parseScala(v)}"""
      case v: Boolean        => s"""${parseScala(v)}"""
      case v: Traversable[_] => s"""${parseCollection(v)}"""
      case v                => throw new IllegalArgumentException("parseValue only support Collection or Primitive types.")
    }
  }

  protected def parseCollection(value: Traversable[_]): String = {
    value match {
      case x: Map[_, _] => x.map(kv => parseLine(kv._1.toString, kv._2)).mkString("\n")
      case x => x.map(v => parseValue(v)).mkString("[", ", ", "]")
    }
  }

  protected def parseScala(value: Any): String = value.toString
}

object ConfigParser extends ConfigParser
