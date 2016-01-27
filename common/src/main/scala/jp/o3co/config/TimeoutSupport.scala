package jp.o3co.config

import scala.concurrent.duration._

trait TimeoutSupport {
  this: Settings => 

  def timeout: FiniteDuration = 
    if(config.hasPath("timeout")) config.getFiniteDuration("timeout")
    else GlobalSettings.Defaults.timeout
}
