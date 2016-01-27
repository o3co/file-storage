package jp.o3co.file.store
package meta 
package dal

import com.typesafe.config.Config

/**
 * 
 */
object DALActor {
  /**
   * Factory to create props of DAL Actor
   * Only support MySQL currently
   */
  def props(config: Config) = {
    DALSettings(config) match {
      case s: BaseH2Settings => H2Actor.props(s)
      case s: MySQLSettings  => MySQLActor.props(s)
      case other => throw new IllegalArgumentException(s"""Unsupported dal type "${other.typename}"""")
    }
  }
}
