package jp.o3co.file.store
package rest

import akka.util.Timeout
import jp.o3co.util.Prepare
import scala.concurrent.ExecutionContext
import spray.routing.HttpService

/**
 *
 */
trait FileTransportSupport extends Implicits with Prepare {

  /**
   *
   */
  def adapter: RESTAdapter
  //= RESTAdapter(actorRefFactory.actorSelection(settings.store))

  /**
   *
   */
  override def prepare {
    if(adapter.isPrepared == false) adapter.prepare
  }
}
