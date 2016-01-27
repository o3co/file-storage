package jp.o3co.rest.config

import com.typesafe.config.Config
import jp.o3co.config.Settings

case class RESTSettings(config: Config) extends Settings with RESTSupport 
