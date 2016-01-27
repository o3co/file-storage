package jp.o3co.file.store

import jp.o3co.config._
import com.typesafe.config.Config
import akka.actor.ActorSystem

object Boot extends App {
  case class BootSettings(
  ) extends ApplicationSettings {
    //override val config: Config = configMap(Map(
    //))
    //  .withFallback(defaultConfig)

    lazy val store = config.getConfig("file.store")
  }


  val parser = new scopt.OptionParser[BootSettings]("file-store-rest") {
    head("file-store", "0.1")

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
}


