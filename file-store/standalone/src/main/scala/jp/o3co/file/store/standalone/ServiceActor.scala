package jp.o3co.file.store
package standalone

import jp.o3co.util.ActorFactory
import akka.actor.Actor
import akka.util.Timeout
import com.typesafe.config.Config
import spray.httpx.Json4sSupport
import jp.o3co.httpx.ReadWriteRequestDelegate
import jp.o3co.httpx.request.delegator._
import spray.routing.HttpService
import spray.util.LoggingContext
import jp.o3co.file.store.rest._

object ServiceActor extends ActorFactory

class ServiceActor(config: Config) extends Actor with ServiceImpl {

  implicit lazy val actorRefFactory = context

  override lazy val settings = ServiceSettings(config)

  implicit lazy val executionContext = context.dispatcher

  implicit def exceptionHandler(implicit log: LoggingContext) = 
    ExceptionHandlerWithJson4s()

  val receive: Receive = runRoute(routeWithPrefix)
}

trait ServiceImpl extends HttpService 
  with rest.FileDownloadRoute
  with rest.FileUploadRoute
  with meta.rest.MetaRoute
  with Json4sSupport
  with ReadWriteRequestDelegate 
{
  implicit val json4sFormats = DefaultFormats

  def settings: ServiceSettings

  val defaultDownloadMode = settings.downloadMode

  implicit lazy val timeout: Timeout = settings.timeout

  setReadWriteRequestDelegator(SimpleRequestDelegator())

  override lazy val adapter = RESTAdapter(
    settings.store, 
    settings.meta
  )

  override def prepare {
    super.prepare
    if(!adapter.isPrepared) adapter.prepare
  }

  def routeWithPrefix = settings.prefix match {
    case Some(p) => pathPrefix(p) { route }
    case None    => route 
  }

  def route = {
    ((settings.isDownloadEnabled, settings.isUploadEnabled) match {
      case (true, true)  => routeFileDownload ~ routeFileUpload
      case (true, false) => routeFileDownload
      case (false, true) => routeFileUpload
      case (_, _)        => throw new Exception("Either download or upload has to be enabled.")
    }) ~
    pathPrefix("metas") {
      routeMeta
    }
  }
}
