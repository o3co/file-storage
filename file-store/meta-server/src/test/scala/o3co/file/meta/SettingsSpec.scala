package jp.o3co.file.store
package meta

import com.typesafe.config.ConfigFactory
import org.specs2.mutable.Specification

class LocalServiceSettingsSpec extends Specification {
  
  "LocalServiceSettings" should {
    "reference dal configuration" in {
      val settings = LocalServiceSettings(ConfigFactory.parseString(
"""
  dal {
    type = "inmem"
  }
"""
        ))

      val dal = settings.dal 

      dal.getString("type") === "inmem"
    }
  }
}
