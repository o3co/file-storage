package jp.o3co.file.store

import jp.o3co.file.store.json4s.Serializers
import jp.o3co.http.HttpException
import spray.http.StatusCodes
import spray.httpx.Json4sSupport
import spray.routing.directives.RouteDirectives._
import spray.routing.{ExceptionHandler => RoutingExceptionHandler}
import spray.util.LoggingContext
import jp.o3co.file.store.json4s.DefaultFormats

/**
 *
 */
package object rest {
  object ExceptionHandlerWithJson4s extends Json4sSupport {

    implicit val json4sFormats = DefaultFormats 

    def apply()(implicit log: LoggingContext): RoutingExceptionHandler = default 

    def apply(f: RoutingExceptionHandler)(implicit log: LoggingContext): RoutingExceptionHandler = {
      f orElse default
    }

    def default(implicit log: LoggingContext): RoutingExceptionHandler = RoutingExceptionHandler {
      case e: meta.IllegalStateException => {
        complete(e.meta.status match {
          case Statuses.UPLOADING => complete(StatusCodes.Processing, s"Resource ${e.meta.id} is still uploading.")
          case Statuses.UPLOADED  => complete(StatusCodes.InternalServerError, s"Please try again.")
          case Statuses.DELETED   => complete(StatusCodes.NotFound, s"Resource ${e.meta.id} is not found.")
          case other              => complete(StatusCodes.InternalServerError, e.getMessage)
        })
      }
      case e: HttpException => {
        complete(e.statusCode, e.getMessage)
      }
    }
  }
}
