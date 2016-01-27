package jp.o3co.file

import java.util.UUID
import java.net.{URI, URL}
//
import jp.o3co.file.store.meta.Meta

/**
 *
 */
package object store {

  /**
   * Resource ID 
   * @param uuid Actual Resource ID 
   */
  case class ResourceId(uuid: UUID) {
    override def toString = uuid.toString
  }

  /**
   * @param name Resource name
   */
  case class ResourceName(name: String) extends AnyVal {
    override def toString = name
  }

  /**
   * @param url Public link url 
   */
  case class Link(url: URL) extends AnyVal {
    override def toString = url.toString
  }

  case class RelativePath(path: String) extends AnyVal {
    override def toString = path
  }

  /**
   *
   * @param uri Stored path
   */
  case class StoredPath(uri: URI) {

    override def toString = uri.toString 

    def segment: SegmentName = SegmentName(uri.getHost) 

    def relativePath: RelativePath = RelativePath(uri.getPath().replaceAll("^\\/", ""))
  }

  /**
   *
   */
  case class Size(value: Int) extends AnyVal 

  /**
   *
   */
  case class Offset(value: Int) extends AnyVal

  // Events and  Models 
  /**
   * @param id
   */
  case class GetResource(id: ResourceId)

  /**
   *
   * @param resource
   */
  case class GetResourceSuccess(resource: Option[StoredResource])

  /**
   * @param cause 
   */
  case class GetResourceFailure(cause: Throwable)

  case class SegmentName(name: String)

  /**
   *
   * @param resource 
   * @param segment Segment name of the resource 
   * @param name Resource name 
   * @param contentType Resource content type 
   */
  case class PutResource(resource: Resource, segment: SegmentName, contentType: ContentType, name: Option[ResourceName] = None)

  /**
   * @param meta Putted resource meta
   */
  case class PutResourceSuccess(meta: Meta)

  /**
   * @param
   */
  case class PutResourceFailure(cause: Throwable)

  /**
   * @param
   */
  case class DeleteResource(id: ResourceId)

  /**
   * @param meta Deleted resource meta
   */
  case class DeleteResourceSuccess(meta: Option[Meta])

  /**
   * @param cause Cause of failure
   */
  case class DeleteResourceFailure(cause: Throwable)

  /**
   * @param
   */
  case class GetLink(id: ResourceId)

  /**
   * @param
   */
  case class GetLinkSuccess(link: Option[Link])

  /**
   * @param cause Cause of failure
   */
  case class GetLinkFailure(cause: Throwable)
}
