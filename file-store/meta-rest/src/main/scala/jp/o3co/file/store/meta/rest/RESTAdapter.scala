package jp.o3co.file.store
package meta
package rest

import akka.actor.ActorSelection
import akka.pattern.ask
import akka.util.Timeout
import jp.o3co.http.HttpException
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import spray.http.StatusCodes
import jp.o3co.file.store.rest.BaseRESTAdapter

/**
 * meta.Service Adapter to provide REST friendly api.
 *
 */
trait RESTAdapter extends BaseRESTAdapter {
  def metas: ActorSelection

  implicit def executionContext: ExecutionContext

  implicit def timeout: Timeout

  override def prepare {
    super.prepare
    if(metas == null) throw new NullPointerException("metas cannot be null.")
  }
  /**
   *
   *
   */
  def getMeta(id: ResourceId): Future[Option[MetaResponse]] = {
    (metas ? GetMeta(id)) map {
      case GetMetaSuccess(None, _) => notFound(id)
      case x: GetMetaSuccess       => Option(MetaResponse(x))
      case GetMetaFailure(cause)   => throw cause
    } recover {
      case e: HttpException => throw e
      case e: Throwable => 
        throw new Exception("Failed to get resource meta.", e)
    //    throw new HttpException(StatusCodes.ServerError, s"Fialed to get Meta [id = $id].", ex)
    }
  }

  def updateMeta(id: ResourceId, request: MetaRequest): Future[MetaResponse] = {
    (metas ? request.toUpdate(id))
      .map {
        case UpdateMetaSuccess(None) => notFound(id)
        case UpdateMetaSuccess(Some(updated)) => (MetaResponse(updated))
        case UpdateMetaFailure(cause)      => throw cause
      }
  }

  /**
   *
   */
  def deleteMeta(id: ResourceId): Future[MetaResponse] = {
    (metas ? DeleteMeta(id))
      .map {
        case DeleteMetaSuccess(None)       => notFound(id)
        case DeleteMetaSuccess(Some(meta)) => (MetaResponse(meta))
        case DeleteMetaFailure(cause)      => throw cause
      }
  }
}

object RESTAdapter {
  def apply(m: ActorSelection)(implicit ec: ExecutionContext, t: Timeout) = {
      new RESTAdapter {
        override val metas = m 
        override val executionContext = ec
        override val timeout = t
      }
  }
}
