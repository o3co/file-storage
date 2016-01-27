package jp.o3co.file.store
package meta
package rest

import jp.o3co.http.HttpException
import spray.http.StatusCodes._

trait Validation {
  def validateMetaForDownload(meta: Meta): Boolean = meta.status match {
    case Statuses.UPLOADING => throw new HttpException(Processing, s"Resource [id = ${meta.id}] is not ready yet.")
    case Statuses.UPLOADED  => true // OK 
    case Statuses.DELETED   => throw new HttpException(Gone, s"Resource [id = ${meta.id}] is gone") 
    case other  => throw new Exception(s"Resource [id = ${other}] is unknown status.")
  }
}
