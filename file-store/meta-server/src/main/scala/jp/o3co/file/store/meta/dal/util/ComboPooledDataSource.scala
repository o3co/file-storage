package jp.o3co.file.store
package meta 
package dal
package util 

import com.mchange.v2.c3p0.{ComboPooledDataSource => C3P0DataSource}

/**
 * Factory of ComboPooledDataSource
 */
object ComboPooledDataSource {
  implicit class AuthenticatedComboPooledDataSource(val underlying: C3P0DataSource) extends AnyVal {
    def addAuthentication(s: SecuredConnectionSupport) = {
      (s.username, s.password) match {
        case (Some(u), Some(p)) => 
          underlying.setUser(u)
          underlying.setPassword(p)
        case (Some(u), None)    => 
          underlying.setUser(u)
        case _ =>
          // nothing to apply
      }
    }
  }
  def apply(settings: JDBCDefinition): C3P0DataSource = {
    val ds = new C3P0DataSource()
    ds.setDriverClass(settings.jdbcDriver)
    ds.setJdbcUrl(settings.jdbcUrl)
    
    if(settings.isInstanceOf[SecuredConnectionSupport]) 
      ds.addAuthentication(settings.asInstanceOf[SecuredConnectionSupport])

    ds
  }
}
