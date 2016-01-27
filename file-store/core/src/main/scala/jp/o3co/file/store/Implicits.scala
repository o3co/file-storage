package jp.o3co.file.store

import scala.language.implicitConversions
import java.util.UUID
import java.net.{URL, URI}

/**
 * Implicits trait  
 */
trait Implicits {

  implicit def resourceIdFromUUID(uuid: UUID): ResourceId = ResourceId(uuid) 

  implicit def resourceIdToUUID(id: ResourceId): UUID = id.uuid

  implicit def resourceNameFromString(name: String): ResourceName = ResourceName(name)

  implicit def resourceNameToString(name: ResourceName): String = name.name

  implicit def linkFromURL(url: URL): Link = Link(url)

  implicit def linkToURL(link: Link): URL = link.url

  implicit def storedPathFromUri(uri: URI): StoredPath = StoredPath(uri)

  implicit def storedPathToUri(path: StoredPath): URI = path.uri

  implicit def statusFromString(name: String): Status = Status(name)

  implicit def statusToString(status: Status): String = status.name

  implicit def segmentFromString(name: String): SegmentName = SegmentName(name)

  implicit def segmentToString(segment: SegmentName): String = segment.name

  implicit def relativePathFromString(path: String): RelativePath = RelativePath(path)

  implicit def relativePathToString(path: RelativePath): String = path.path

  implicit def sizeFromInt(value: Int): Size = Size(value)

  implicit def sizeToInt(size: Size): Int = size.value

  implicit def offsetFromInt(value: Int): Offset = Offset(value)

  implicit def offsetToInt(offset: Offset): Int = offset.value

  implicit def contentTypeFromString(name: String): ContentType = ContentType(name)

  implicit def contentTypeToString(contentType: ContentType): String = contentType.toString

  implicit def contentTypeFromSprayContentType(from: spray.http.ContentType): ContentType = ContentType(from.mediaType.mainType, from.mediaType.subType) 

  implicit def resourceToByteArray(resource: Resource): Array[Byte] = resource.toBytes

}

object Implicits extends Implicits 
