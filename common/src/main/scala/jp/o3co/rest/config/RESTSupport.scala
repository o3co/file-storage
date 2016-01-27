package jp.o3co.rest.config

import jp.o3co.config._

/**
 * REST configuration preset
 */
trait RESTSupport extends TimeoutSupport {
  this: Settings => 

  def host: String = config.getOption[String]("host").getOrElse("localhost")

  def port: Int    = config.getOption[Int]("port").getOrElse(8080)
}

