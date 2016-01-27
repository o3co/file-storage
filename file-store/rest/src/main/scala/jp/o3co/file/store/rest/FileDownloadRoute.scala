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


trait FileDownloadRoute extends FileTransportSupport {
  this: HttpService with ReadWriteRequestDelegate => 

  def defaultDownloadMode: DownloadMode

  /**
   *
   */
  def routeFileDownload: Route = {
    path(IDSegment) { id =>
      // [GET] /{id}
      get {
        parameters("mode".?) { mode => 
          mode.map(_ => DownloadMode(_)).getOrElse(defaultDownloadMode) match {
            case DownloadModes.Redirect => 
              // Redirect to the public_path
              delegateRead(adapter.getLinkFromResourceId(id)) {
                link => redirect(link.toString, StatusCodes.Found)
              }
            case DownloadModes.Proxy  => 
              delegateRead(adapter.getResourceFromResourceId(id)) {
                case resource: StoredResource => 
                  respondWithMediaType(resource.meta.contentType.toMediaType) {
                    complete(HttpResponse(entity = HttpEntity(ContentType(resource.meta.contentType.toMediaType, None), resource.toBytes))) 
                  } 
                case resource => throw new Exception("none") 
              }
            case other => 
              throw new Exception(s"""Unknown Download mode "$other"""")
          }
        }
      }
    } 
  }
}

