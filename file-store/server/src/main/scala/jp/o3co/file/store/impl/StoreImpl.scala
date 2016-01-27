package jp.o3co.file.store
package impl

import akka.actor.{ActorRef, ActorSelection, ActorRefFactory}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import scala.concurrent.{ExecutionContext, Future}

import jp.o3co.file.store.meta._
import jp.o3co.util.Prepare
import link.LinkResolver
import storage.FileStorage

/**
 *
 */
trait StoreImpl extends Store with LazyLogging with Prepare {

  implicit def actorRefFactory: ActorRefFactory

  implicit def executionContext: ExecutionContext

  def settings: LocalStoreSettings 

  lazy val metas = meta.ServiceActor(settings.meta)("meta")

  /**
   * Initialize segments and storage 
   */
  lazy val segments: Segments = Segments(settings.segments)
 
  implicit val linkResolver = LinkResolver(segments, settings.timeout) 

  implicit val timeout: Timeout = settings.timeout

  override def prepare {
    super.prepare
    storage.FileStorages.boot(segments)
    println(metas)
  }

  //def init: Unit = {
  //  storage.FileStorages.boot(segments)
  //}

  /**
   *
   * @throw IllegalStateException Resource is not ready yet 
   * @throw 
   */
  def getLink(id: ResourceId): Future[Option[Link]] = {
    getMetaForPublic(id)
      .map { meta => 
        meta match {
          case Some(m) => m.storedPath.flatMap(path => linkResolver(path))
          case None    => None
        }
      }
  }

  /**
   *
   */
  protected def getResource(id: ResourceId): Future[Option[StoredResource]] = {
    getMetaForPublic(id)
      .flatMap { metaOpt => 
        metaOpt.map { m =>
          loadResourceForMeta(m).map { resource => 
            resource match {
              case Some(r) => Option(StoredResource(r, m))
              case None    => {
                logger.info(s"""Resource for Meta "$m.id" is not found.""")
                None
              }
            }
          }
        }
        .getOrElse(Future(None))
      }
  }

  /**
   *
   */
  protected def getMetaForPublic(id: ResourceId): Future[Option[Meta]] = {
    (metas ? GetMeta(id, false))
      .map {
        case GetMetaSuccess(Some(meta), _) =>
          if(!meta.isReady) throw new IllegalStateException(meta)
          else Option(meta)
        case GetMetaSuccess(None, _)    => None
        case GetMetaFailure(cause)   => throw cause
      }
      .recover {
        case e: IllegalStateException => throw e
        case e: Throwable => throw new Exception("Fialed to get FileMeta", e)
      }
  }

  /**
   *
   */
  def loadResourceForMeta(meta: Meta): Future[Option[Resource]] = {
    meta.status match {
      case Statuses.UPLOADED => 
        val path = meta.storedPath.getOrElse(throw new IllegalStateException(meta, "StoredPath is not specified for uploaded meta."))
        (segments(path.segment).storage ? FileStorage.LoadResource(path.relativePath))
          .map {
            case FileStorage.LoadResourceResult(loaded) => loaded
          }
          .recover {
            // Case exception is occured on future 
            case e => throw new Exception("Failed to get StoredFile.", e)
          } 
      case _ => throw new IllegalStateException(meta)
    }
  }

  /**
   * Save resource async
   */
  def putResource(resource: Resource, segmentName: SegmentName, contentType: ContentType, name: Option[ResourceName] = None): Future[Meta] = {
    // Register Meta 
    val fMeta = (metas ? RegisterMeta(name = name, contentType = contentType))
      .map {
        case RegisterMetaSuccess(meta) => meta
        case RegisterMetaFailure(ex)   => throw ex 
      }
      .recover {
        case e => throw e
      }

    fMeta.onSuccess { 
      case meta => logger.info(s"Meta ${meta.id} is registered.")
    }

    // convert content byte[] to TemporaryFileResource.
    // TemporaryFileResource is cheaper than ArrayByteResource when over protocol, 
    val tempfile = TemporaryFileResource(resource)
    val segment  = segments(segmentName)
    fMeta.map { meta => 
      logger.info(s"""Try store resource for meta ${meta.id} on storage "$segmentName"""")
      val storage = segment.storage
      val name    = segment.naming(meta)

      val stored = (storage ? FileStorage.SaveResource(tempfile, name))
      stored.onSuccess { 
        case FileStorage.SaveResourceResult(link) => logger.info(s"""Resource for Meta ${meta.id} is stored on storage at "${link}".""")
      }

      stored
        .flatMap {
          case FileStorage.SaveResourceResult(_) => 
            (metas ? meta.update(storedPath = Option(Segment.path(segmentName, name)), status = Statuses.UPLOADED))
              .map {
                case UpdateMetaSuccess(uploaded) => 
                  logger.warn(s"Complete to upload")
                  // processor ! Trigger(trigEvent, uploaded)
              }
              .recover {
                case e => logger.error("Failed to upload completion", e)
              }
        }
        .recover {
          case e => logger.error("Failed to store resource", e)
        }
    }

    // return meta as the result of the saving attachment, synchronously
    fMeta 
  }
}
