package jp.o3co.file.store
package rest

import akka.actor.{Actor, ActorSelection, ActorPath, ActorRefFactory, Props}
import akka.util.Timeout
import com.typesafe.config.Config
import jp.o3co.httpx.ReadWriteRequestDelegate
import jp.o3co.httpx.request.delegator.SimpleRequestDelegator
import jp.o3co.rest.config.RESTSettings
import jp.o3co.util.ActorFactory
import spray.httpx.Json4sSupport
import spray.routing._
import spray.util.LoggingContext
import jp.o3co.file.store.json4s.DefaultFormats


object ServiceActor extends ActorFactory 

/**
 * REST ServiceActor 
 */
class ServiceActor(config: Config) extends Actor with ServiceImpl {

  implicit lazy val actorRefFactory = context

  override lazy val settings = ServiceSettings(config)

  implicit def exceptionHandler(implicit log: LoggingContext) = 
    ExceptionHandlerWithJson4s()

  val receive = runRoute(route)
}

/**
 * Actual Service Implementation
 */
trait ServiceImpl extends HttpService 
  with Json4sSupport 
  //with FileTransportRoute
  with FileDownloadRoute
  with FileUploadRoute
  with ReadWriteRequestDelegate
{

  def settings: ServiceSettings

  implicit lazy val timeout: Timeout = settings.timeout

  override val defaultDownloadMode = settings.downloadMode

  /**
   * Json4s Formats to mixin Json4sSupport 
   */
  implicit val json4sFormats = DefaultFormats

  implicit lazy val executionContext = actorRefFactory.dispatcher

  setReadWriteRequestDelegator(SimpleRequestDelegator())

  override lazy val adapter = RESTAdapter(settings.store)

  // update route
  def routeWithPrefix: Route = 
    settings.prefix match {
      case Some(p) => pathPrefix(p) { route }
      case None    => route
    }

  def route: Route = 
    (settings.isDownloadEnabled, settings.isUploadEnabled) match {
      case (true, true)  => routeFileDownload ~ routeFileUpload
      case (true, false) => routeFileDownload
      case (false, true) => routeFileUpload
      case (_, _)        => throw new Exception("Either download or upload has to be enabled.")
    }
}


