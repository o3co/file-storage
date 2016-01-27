package jp.o3co.file.store.meta.dal
package impl

//import slick.jdbc.JdbcBackend.Database
import jp.o3co.file.store.meta.dal.util.ComboPooledDataSource
import scala.concurrent.Await

/**
 *
 */
trait MySQLImpl extends SlickDriver {

  def settings: MySQLSettings

  override val profile = slick.driver.MySQLDriver

  import profile.api._

  override lazy val database = Database.forDataSource(ComboPooledDataSource(settings))

  settings.initAction match {
    case Some(DBActions.CreateTable) => Await.ready(createTables, settings.initTimeout)
    case _ => 
  }
}
