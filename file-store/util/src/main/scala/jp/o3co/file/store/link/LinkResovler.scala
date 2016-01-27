package jp.o3co.file.store
package link

import akka.actor.ActorRefFactory
import akka.pattern.ask
import akka.util.Timeout
import jp.o3co.file.store.storage.FileStorage
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}

case class LinkResolver(val segments: Segments, val timeout: FiniteDuration)(implicit val actorRefFactory: ActorRefFactory, ec: ExecutionContext) {
  
  implicit val actorTimeout: Timeout = timeout

  def apply(path: StoredPath): Option[Link] = {
    (segments.get(path.segment).flatMap {segment => 
      Await.result(
        (segment.storage ? FileStorage.ResolveLink(path.relativePath))
          .map {
            case FileStorage.ResolveLinkResult(link) => link
          },
        timeout
      )
    })
  }
}
