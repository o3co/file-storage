package jp.o3co.config

import com.typesafe.config.Config

trait Settings extends Implicits {

  /**
   * Underlying Config 
   */
  def config: Config
}

