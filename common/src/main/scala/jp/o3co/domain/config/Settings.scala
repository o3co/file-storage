package jp.o3co.domain.config

import com.typesafe.config.Config
import scala.concurrent.duration._
import jp.o3co.config._
import jp.o3co.config.{Settings => BaseSettings}

trait Settings extends BaseSettings with TimeoutSupport {

  def servicePath(name: String) = config.getString(name)
}

