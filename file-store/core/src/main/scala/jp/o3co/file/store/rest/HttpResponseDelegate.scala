package jp.o3co.file.store
package rest

import jp.o3co.util.Prepare
import jp.o3co.http.HttpException
import spray.http.StatusCodes

trait BaseRESTAdapter extends Prepare {
  
  protected def notFound(id: ResourceId) = throw new HttpException(StatusCodes.NotFound, s"Meta [id = $id] is not exits.")
}
