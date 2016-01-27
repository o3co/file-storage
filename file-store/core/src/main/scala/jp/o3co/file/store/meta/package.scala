package jp.o3co.file.store

import java.util.UUID
import java.util.Date

/**
 *
 */
package object meta {

  /**
   *
   */
  case class Meta(
    id: ResourceId, 
    //segment: SegmentName, 
    contentType: ContentType,
    name: Option[ResourceName],
    storedPath: Option[StoredPath],
    status: Status,
    created: Date,
    updated: Date,
    parentId: Option[ResourceId]
  ) {
    def segment: Option[SegmentName] = storedPath.map(p => p.segment)

    def isReady: Boolean = status match {
      case Statuses.UPLOADED => true
      case _ => false
    }

    def update(
      //segment: SegmentName = this.segment,
      name: Option[ResourceName] = this.name,
      storedPath: Option[StoredPath] = this.storedPath,
      contentType: ContentType = this.contentType,
      status: Status = this.status
    ): UpdateMeta = {
      UpdateMeta(
        id = this.id,
        //segment = segment,
        contentType= contentType,
        name = name,
        storedPath = storedPath,
        status = status 
      )
    }

    /**
     *
     */
    def updateWith(update: UpdateMeta) = {
      this.copy(
        id      = this.id,
        //segment = update.segment, 
        storedPath = update.storedPath,
        status = update.status,
        created = this.created,
        updated = new Date()
      )
    }

    /**
     *
     */
    def updateWith(update: BatchUpdateMeta) = {
      this.copy(
        id          = this.id,
        //segment     = update.segment.getOrElse(this.segment),
        contentType = update.contentType.getOrElse(this.contentType),
        name        = update.name.orElse(this.name),
        storedPath  = update.storedPath.orElse(this.storedPath),
        status  = this.status,
        created = this.created,
        updated = new Date()
      )
    }
  }

  /**
   *
   */
  type Relatives = Map[SegmentName, ResourceId]

  /*
   * Model to register new Meta 
   *
   */
  case class RegisterMeta(
    //segment: SegmentName,
    contentType: ContentType,
    name: Option[ResourceName] = None,
    storedPath: Option[StoredPath] = None,
    status: Status = Statuses.UPLOADING,
    parentId: Option[ResourceId] = None
  ) {
    
    def withId(id: ResourceId): Meta = {
      Meta(id, contentType, name, storedPath, status, new Date(), new Date(), parentId)
    }
  }

  /**
   *
   */
  case class RegisterMetaSuccess(meta: Meta)

  case class RegisterMetaFailure(cause: Throwable)

  /**
   *
   */
  case class UpdateMeta(
    id: ResourceId,
    //segment: SegmentName,
    contentType: ContentType,
    name: Option[ResourceName],
    storedPath: Option[StoredPath],
    status: Status
  )

  /**
   *
   */
  case class BatchUpdateMeta(
    id: ResourceId,
    //segment: Option[SegmentName] = None,
    contentType: Option[ContentType] = None,
    name: Option[ResourceName] = None,
    storedPath: Option[StoredPath] = None,
    status: Option[Status] = None
  )

  /**
   * @param meta if meta exists, updated Meta. Otherwise None
   */
  case class UpdateMetaSuccess(meta: Option[Meta])

  case class UpdateMetaFailure(cause: Throwable)

  /**
   * @param id 
   */
  case class ContainsMeta(id: ResourceId)

  /**
   * @param isContains
   */
  case class ContainsMetaSuccess(isContains: Boolean)

  case class ContainsMetaFailure(cause: Throwable)

  /**
   *
   * @param id Resource id
   * @param includeRelatives Include relatives on response or not
   */
  case class GetMeta(id: ResourceId, includeRelatives: Boolean = false)

  /**
   * @param meta Some if exists, None otherwise
   * @param relatives Segment vs ResourceId Map if includeRelatives is true, None otherwise.
   */
  case class GetMetaSuccess(meta: Option[Meta], relatives: Option[Relatives])

  case class GetMetaFailure(cause: Throwable)

  /**
   * @param id ResourceId to delete
   */
  case class DeleteMeta(id: ResourceId)

  /**
   * @param deleted Some if exits, None otherwise.
   */
  case class DeleteMetaSuccess(deleted: Option[Meta])

  case class DeleteMetaFailure(cause: Throwable)
}

