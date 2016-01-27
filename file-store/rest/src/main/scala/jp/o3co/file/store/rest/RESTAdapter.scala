package jp.o3co.file.store.rest

import akka.actor.ActorSelection
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.{ExecutionContext, Future}
import java.net.URL
import spray.http.StatusCodes

import jp.o3co.http.HttpException
import jp.o3co.file.store.meta.Meta
import jp.o3co.file.store._
import jp.o3co.file.store.storage.FileStorage._

trait RESTAdapter extends BaseRESTAdapter with Implicits {

  val store: ActorSelection

  implicit def timeout: Timeout

  implicit def executionContext: ExecutionContext

  def getLinkFromResourceId(id: ResourceId): Future[Link] = {
    (store ? GetLink(id))
      .map {
        case GetLinkSuccess(Some(link)) => link
        case GetLinkSuccess(None)       => notFound(id)
        case GetLinkFailure(ex)         => throw ex
      }
  }

  def getResourceFromResourceId(id: ResourceId): Future[StoredResource] = {
    (store ? GetResource(id))
      .map {
        case GetResourceSuccess(Some(resource)) => resource
        case GetResourceSuccess(None)           => notFound(id)
        case GetResourceFailure(ex)             => throw ex
      }
  }

  /**
   * 
   * {{{
   *  post {
   *    respondWithMediaType(`application/json`) {
   *      entity(as[MultipartFormData]) { formData =>
   *        detach() {
   *          complete {
   *            formData.fields.map {
   *              case (name, BodyPart(entity, headers)) =>
   *                val content = new ByteArrayInputStream(entity.buffer)
   *                val contentType = headers.find(h => h.is("content-type")).get.value
   *                val fileName = headers.find(h => h.is("content-disposition")).get.value.split("filename=").last
   *                // save attached file
   *                saveAttachment(fileName, content)
   *            }
   *            .map { fMetas =>  
   *              // Future[Seq[Meta]]
   *              Future.sequence(fMetas)
   *            }
   *          }
   *        }
   *      }
   *    }
   *  }
   * }}}
   */
  def saveFileResource(contentType: ContentType, content: Array[Byte], filename: Option[ResourceName] = None, segment: SegmentName = null): Future[Meta] = {
    (store ? PutResource(TemporaryFileResource(content), Option(segment).getOrElse("default"), contentType, filename))
      .map {
        case PutResourceSuccess(m)  => m
        case PutResourceFailure(ex) => throw new Exception("Failed to save resource", ex)
      }
  }
}

object RESTAdapter {
  def apply(service: ActorSelection)(implicit ec: ExecutionContext, to: Timeout) = new RESTAdapter {
    override val store = service
    override val executionContext = ec
    override val timeout  = to
  }
}

