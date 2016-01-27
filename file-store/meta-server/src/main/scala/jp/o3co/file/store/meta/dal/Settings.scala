package jp.o3co.file.store
package meta 
package dal

import com.typesafe.config.Config
import java.net.URL
import scala.concurrent.duration.FiniteDuration
import jp.o3co.config._

object DALSettings {
  def apply(baseConfig: Config): DatabaseDefinition = {
    baseConfig.getString("type").toLowerCase match {
      case "mysql" => MySQLSettings(baseConfig)
      case "h2mem" => H2MemSettings(baseConfig)
      case _ => new DatabaseDefinition {
        override val config = baseConfig
      }
    }
  }
}

trait DBAction
object DBAction {
  def apply(name: String): DBAction = {
    name.toLowerCase match {
      case "create" => DBActions.CreateTable
      case "drop"   => DBActions.DropTable
      case "purge"  => DBActions.PurgeTable
    }
  }
}
object DBActions {
  case object CreateTable extends DBAction
  case object DropTable extends DBAction
  case object PurgeTable extends DBAction
}

trait Settings {
  def config: Config

  def typename: String = config.getString("type")
}

trait DatabaseDefinition extends Settings {
  def initTimeout: FiniteDuration = config.getFiniteDuration("init-timeout")

  def initAction: Option[DBAction] = 
    if(config.hasPath("init-action")) {
      Option(DBAction(config.getString("init-action")))
    } else {
      None
    }
}

trait SecuredConnectionSupport extends Settings {

  def username: Option[String] = 
    if(config.hasPath("username")) Option(config.getString("username"))
    else None

  def password: Option[String] = 
    if(config.hasPath("password")) Option(config.getString("password"))
    else None
}

trait JDBCDefinition {
  this: DatabaseDefinition => 

  def jdbcUrl: String 

  def jdbcDriver: String 
}

trait BaseH2Settings extends DatabaseDefinition with JDBCDefinition {
  def jdbcDriver: String = "org.h2.Driver"

  def dbname: String = config.getString("dbname")
}

case class H2Settings(override val config: Config) extends BaseH2Settings with SecuredConnectionSupport {

  def jdbcUrl: String = s"jdbc:h2:mem:$dbname"
}

case class H2MemSettings(override val config: Config) extends BaseH2Settings {

  def jdbcUrl: String = s"jdbc:h2:mem:$dbname"
}

case class MySQLSettings(override val config: Config) extends DatabaseDefinition with JDBCDefinition with SecuredConnectionSupport {
  
  val DEFAULT_HOST = "localhost"

  val DEFAULT_PORT = 3306

  /**
   *
   */
  def jdbcUrl = s"jdbc:mysql://$host:$port/$dbname"

  def jdbcDriver: String = "com.mysql.jdbc.Driver"

  /**
   *
   */
  def host: String = 
    if(config.hasPath("host")) config.getString("host")
    else DEFAULT_HOST

  def port: Int = 
    if(config.hasPath("port")) config.getInt("port")
    else DEFAULT_PORT

  def dbname: String = config.getString("dbname")
}
