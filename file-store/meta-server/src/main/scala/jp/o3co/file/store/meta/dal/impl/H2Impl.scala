package jp.o3co.file.store.meta.dal 
package impl 

//import slick.jdbc.JdbcBackend.Database
import jp.o3co.file.store.meta.dal.util.ComboPooledDataSource
import scala.concurrent.Await

trait H2Impl extends SlickDriver {

  def settings: BaseH2Settings

  override lazy val profile = slick.driver.H2Driver

  import profile.api._

  override lazy val database = Database.forDataSource(ComboPooledDataSource(settings))

  settings.initAction match {
    case Some(DBActions.CreateTable) => Await.ready(createTables, settings.initTimeout)
    case _ => 
  }
}

