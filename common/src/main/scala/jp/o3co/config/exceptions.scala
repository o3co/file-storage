package jp.o3co.config

case class ConfigurationException(message: String = null, cause: Throwable = null) extends RuntimeException(message, cause)
