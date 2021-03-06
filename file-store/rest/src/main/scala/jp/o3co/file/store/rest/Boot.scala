package jp.o3co.file.store
package rest

import akka.actor.ActorSystem
import akka.actor.Props
import akka.io.IO
import akka.pattern.ask
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.Config
import jp.o3co.config._
import jp.o3co.rest.config.RESTSettings
import spray.can.Http

object Boot extends App {
  case class BootSettings(
    host: Option[String] = None,
    port: Option[Int]    = None
  ) extends ApplicationSettings {
    override val config: Config = configMap(Map(
      "file.store.rest.host"  -> host,
      "file.store.rest.port"  -> port
    ))
      .withFallback(defaultConfig)

    lazy val rest = RESTSettings(config.getConfig("file.store.rest"))

    lazy val store = config.getConfig("file.store")
  }


  val parser = new scopt.OptionParser[BootSettings]("file-store-rest") {
    head("file-store-rest", "0.1")

    opt[String]('h', "host") action {(v, s) => s.copy(host = Some(v))} text("Hostname of the REST service.")
    opt[Int]('p', "port") action {(v, s) => s.copy(port = Some(v))} text("Port number of the REST service.")
    cmd("show-config") action {(_, s) => 
      println(s.config.getConfig("file").root.render())
      sys.exit()
    } text("")
  }

  val settings: BootSettings = parser.parse(args, BootSettings()) match {
    case Some(s) => s
    case None    => 
      parser.showUsage
      sys.exit()
  }

  implicit val system = ActorSystem("system", settings.config)

  val service = StoreActor(settings.store)("filestore")

  val restService  = ServiceActor(settings.rest.config, service.path)()
  //system.actorOf(Props(classOf[ServiceActor], settings.rest.config, service.path))

  implicit val timeout: Timeout = settings.rest.timeout

  IO(Http) ? Http.Bind(restService, interface = settings.rest.host, port = settings.rest.port)
}

