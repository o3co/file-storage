package jp.o3co.file.store
package meta
package rest

import jp.o3co.config._
import com.typesafe.config.Config
import akka.actor.ActorSystem

import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout

object Boot extends App {
  /**
   * This boot settings 
   */
  case class BootSettings(
    host: Option[String] = None,
    port: Option[Int] = None
  ) extends ApplicationSettings {

    override def config: Config = configMap(Map(
        "file.store.meta.rest.host" -> host,
        "file.store.meta.rest.port" -> port 
      ))
        .withFallback(defaultConfig)

    def meta: Config = config.getConfig("file.store.meta")

    def rest: Config = config.getConfig("file.store.meta.rest")

    def segments: Config = config.getConfig("file.store.segments")
  }

  val parser = new scopt.OptionParser[BootSettings]("file-meta-rest") {
    head("file-store-meta-rest", "0.1")
    opt[String]('h', "host") action {(v, s) => s.copy(host = Some(v)) } text("hostname of this server")
    opt[Int]('p', "port") action {(v, s) => s.copy(port = Some(v)) } text("port number of REST API")
  }

  val settings: BootSettings = parser.parse(args, BootSettings()) match {
    case Some(s) => s
    case None    => 
      parser.showUsage
      sys.exit()
  }

  // 
  implicit val system = ActorSystem("system", settings.config)

  //// Boot Service 
  //val metaService = ServiceActor(settings.meta)("meta")

  // Boot REST service and Bind into Http
  val rest = ServiceActor(settings.rest)("rest")

  implicit val timeout: Timeout = GlobalSettings.Defaults.timeout
  IO(Http) ? Http.Bind(rest, interface = settings.rest.getString("host"), port = settings.rest.getInt("port"))
}
