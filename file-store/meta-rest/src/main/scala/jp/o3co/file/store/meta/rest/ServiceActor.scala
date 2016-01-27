package jp.o3co.file.store.meta
package rest

import akka.actor.{Actor, ActorRefFactory}
import akka.util.Timeout
import com.typesafe.config.Config
import org.json4s.Formats
import scala.concurrent.ExecutionContext
import spray.http.MediaTypes._
import spray.httpx.Json4sSupport
import spray.routing._
import spray.routing.RoutingSettings
import spray.routing.{ExceptionHandler => RoutingExceptionHandler}
import jp.o3co.http.HttpException
import spray.util.LoggingContext
import jp.o3co.httpx.ReadWriteRequestDelegate
import jp.o3co.httpx.request.delegator._

import jp.o3co.file.store.util._
import jp.o3co.util.ActorFactory

object ServiceActor extends ActorFactory 

/**
 *
 */
class ServiceActor(config: Config) extends Actor 
  with ServiceImpl 
{
  implicit lazy val actorRefFactory = context

  override lazy val settings = ServiceSettings(config)

  implicit def serviceExceptionHandler(implicit log: LoggingContext): RoutingExceptionHandler = 
    ExceptionHandler()

  //implicitly[RoutingSettings]

  def receive: Receive = runRoute(routeWithPrefix)
}

trait ServiceImpl extends HttpService 
  with Json4sSupport 
  with ReadWriteRequestDelegate
  with MetaRoute 
{
  def settings: ServiceSettings

  implicit val json4sFormats: Formats = DefaultFormats 

  implicit def executionContext: ExecutionContext = actorRefFactory.dispatcher

  implicit lazy val timeout: Timeout = settings.timeout

  override lazy val adapter = RESTAdapter(settings.meta)

  setReadWriteRequestDelegator(SimpleRequestDelegator())

  def routeWithPrefix = settings.prefix match {
    case Some(p) => pathPrefix(p) { route }
    case None    => route
  }

  // update route
  val route: Route = pathEndOrSingleSlash {
      get {
        respondWithMediaType(`application/json`) {
          complete("success")
        }
      }
    } ~ pathPrefix("metas") {
      routeMeta
    }
}
