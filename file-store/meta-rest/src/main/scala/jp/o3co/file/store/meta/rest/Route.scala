package jp.o3co.file.store
package meta
package rest

import akka.actor.ActorPath
import akka.util.Timeout
import java.net.{URI, URL}
import java.util.Date
import java.util.UUID
import jp.o3co.http.HttpException
import jp.o3co.httpx.ReadWriteRequestDelegate
import jp.o3co.util.Prepare
import scala.concurrent.ExecutionContext
import scala.util.{Success, Failure}
import spray.http.MediaTypes._
import spray.http.StatusCodes
//import spray.httpx.Json4sSupport
import spray.httpx.unmarshalling.Unmarshaller
import spray.httpx.marshalling.Marshaller
import spray.routing._
//import jp.o3co.file.store.SegmentName
//
import Matchers._

trait MetaRoute extends Prepare {
  this: HttpService with ReadWriteRequestDelegate => 
  //this: HttpService with Json4sSupport with ReadWriteRequestDelegate => 

  /**
   * Timeout of the services 
   */
  implicit def timeout: Timeout

  /**
   * ExecutionContext to map futures 
   */
  implicit def executionContext: ExecutionContext

  /**
   * RESTAdapter
   */
  //private lazy val adapter: RESTAdapter
  def adapter: RESTAdapter

  /**
   * {@inheritDoc}
   */
  override def prepare {
    super.prepare
    // load lazy-load adapter on initialization 
    if(!adapter.isPrepared) adapter.prepare
  }

  def routeMeta(implicit mmr: Marshaller[MetaResponse], umr: Unmarshaller[MetaRequest]): Route = {
    //pathEndOrSingleSlash {
    //  //// [GET] / : list of metas 
    //  //get {
    //  //  metas.get
    //  //} ~
    //  post {
    //    val meta = entity(as[MetaRequest])
    //    
    //    metas.get(meta.id).flatMap { target => 
    //      val update = target.copy(
    //        status = meta.status.getOrElse(target.status)
    //      )
    //      metas.put(update).map(_ => update)
    //    }
    //  }
    //} ~
    path(IDSegment) { id => 
      // [GET] /{id} : Get meta detail
      get {
        respondWithMediaType(`application/json`) { 
          delegateRead(adapter.getMeta(id)) {
            case Some(meta) => complete(meta)
            case _ => notFound(id)
          }
        }
      } ~
      // [POST] /{id} : Update file meta 
      post {
        entity(as[MetaRequest]) { meta => 
          respondWithMediaType(`application/json`) {
            delegateWrite(adapter.updateMeta(id, meta)) {
              case meta: MetaResponse => complete(meta)
            }
          }
        }
      //} ~ 
      //delete {
      //  respondWithMediaType(`application/json`) {
      //    delegateWrite(adapter.deleteMeta(id)) {
      //      case deleted: MetaResponse => complete(deleted)
      //    }
      //  }
      }
    } 
  }

  def notFound(id: ResourceId) = throw new HttpException(StatusCodes.NotFound, s"Resource $id is not found.")
}
