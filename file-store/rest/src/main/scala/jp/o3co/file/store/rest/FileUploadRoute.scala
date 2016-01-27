package jp.o3co.file.store
package rest

import java.io.ByteArrayInputStream
import jp.o3co.file.store.meta._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Failure}
import spray.http._
import spray.http.MediaTypes._
import spray.httpx.marshalling.Marshaller
import spray.routing._
import spray.httpx.Json4sSupport 

import jp.o3co.file.store.meta.rest.Matchers.IDSegment
import jp.o3co.httpx.ReadWriteRequestDelegate

trait FileUploadRoute extends FileTransportSupport {
  this: HttpService with ReadWriteRequestDelegate => 

  /**
   *
   */
  def routeFileUpload(implicit m: Marshaller[Map[String, Meta]]): Route = {
    pathEndOrSingleSlash {
      // [POST] /
      post {
        //entity.contentType.mediaType match {
        //  case `multipart/form-data` =>
        entity(as[MultipartFormData]) { formData =>
          respondWithMediaType(`application/json`) {
            detach() {
              delegateWrite (
                Future.sequence(formData.fields
                  // filter only nonEmpty entity
                  .collect {
                    case part: BodyPart if !part.entity.isEmpty => (part.name.getOrElse(""), (part.filename, part.entity.toOption.get, part.headers))
                  }
                  .map {
                    case (fieldName, (filename, entity, headers)) => 
                      val content = entity.data.toByteArray
                      val contentType = entity.contentType

                      // Future[FieldName, Meta]
                      adapter.saveFileResource(contentType, content, filename.map(n => ResourceName(n)))
                        .map(res => (fieldName, res))
                  }
                )
                // Future[Seq[(FieldName, Meta)]] -> Future[Map[FieldName, Meta]]
                .map( ret => ret.toMap)
              ) { ret => 
                complete(ret)
              }
            }
          }
        }
      }
    } 
  }
}

