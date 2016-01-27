package jp.o3co.file.store
package meta

import java.util.{Date, UUID}
import java.net.URL
import jp.o3co.http.HttpException
import org.json4s.{Formats, DefaultFormats => Json4sDefaultFormats}
import scala.language.implicitConversions
import spray.http.{StatusCode, StatusCodes}
import spray.httpx.Json4sSupport
import spray.httpx.marshalling.Marshaller
import spray.routing._
import spray.routing.directives.MiscDirectives._
import spray.routing.directives.RouteDirectives._
import spray.routing.{ExceptionHandler => RoutingExceptionHandler}
import spray.util.LoggingContext
import jp.o3co.file.store.util.URITransformer
import jp.o3co.file.store.json4s.Serializers

//import Implicits._

package object rest extends Implicits {

  //val DefaultFormats: Formats = Json4sDefaultFormats ++ Serializers.all
  val DefaultFormats: Formats = Json4sDefaultFormats ++ Serializers.all

  object ExceptionHandler extends Json4sSupport {

    implicit val json4sFormats = DefaultFormats 

    def apply()(implicit log: LoggingContext): RoutingExceptionHandler = default 

    def apply(f: RoutingExceptionHandler)(implicit log: LoggingContext): RoutingExceptionHandler = {
      f orElse default
    }

    def default(implicit log: LoggingContext): RoutingExceptionHandler = RoutingExceptionHandler {
      case e: HttpException => {
        complete(e.statusCode, e.getMessage)
      }
    }
  }
  
  def metaStatusToStatusCode: Status => StatusCode = {
    case Statuses.UPLOADING => StatusCodes.Processing 
    case Statuses.UPLOADED  => StatusCodes.OK
    case Statuses.DELETED   => StatusCodes.NotFound
    case other => throw new IllegalArgumentException(s"""Status "$other" is unknown status.""")
  }

  case class MetaRequest(
    //segment: Option[String],
    contentType: Option[String],
    name: Option[String]
  ) {

    //def toRegister = {
    //  RegisterMeta(
    //    //segment     = segment.get,
    //    contentType = contentType.get,
    //    name        = name.map(n => ResourceName(n)),
    //    storedPath  = None,
    //    status      = Statuses.UPLOADING,
    //    parentId    = None
    //  )
    //}
    
    /**
     * RESTMetaRequest only support following to modify
     *   - "name" 
     *
     */
    def toUpdate(id: ResourceId) = {
      BatchUpdateMeta(
        id          = id,
        name        = name.map(n => n: ResourceName)
      )
    }
  }

  case class MetaResponse(
    id: ResourceId,
    segment: Option[SegmentName],
    contentType: ContentType, 
    name: Option[ResourceName],
    status: Status,
    created: Date,
    updated: Date,
    relatives: Option[Map[String, UUID]] 
  ) 

  object MetaResponse {
    def apply(result: GetMetaSuccess): MetaResponse = MetaResponse(
      id          = result.meta.get.id,
      segment     = result.meta.get.storedPath.map(p => p.segment),
      contentType = result.meta.get.contentType,
      name        = result.meta.get.name.map(n => n:String),
      status      = result.meta.get.status,
      created     = result.meta.get.created,
      updated     = result.meta.get.updated,
      relatives   = result.relatives.map{ items => 
        items.map { kv => (kv._1: String, kv._2: UUID) } 
      }
    )

    def apply(meta: Meta): MetaResponse = MetaResponse(
      id          = meta.id,
      segment     = meta.storedPath.map(p => p.segment),
      contentType = meta.contentType,
      //link        = meta.link,
      name        = meta.name,
      status      = meta.status,
      created     = meta.created,
      updated     = meta.updated,
      relatives   = None
    )
  }
}
