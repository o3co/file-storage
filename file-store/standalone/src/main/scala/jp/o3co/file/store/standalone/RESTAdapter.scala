package jp.o3co.file.store
package standalone

import akka.actor.ActorSelection
import akka.util.Timeout
import jp.o3co.file.store.meta
import scala.concurrent.ExecutionContext

trait RESTAdapter extends rest.RESTAdapter with meta.rest.RESTAdapter 


object RESTAdapter {
  def apply(storeSelection: ActorSelection, metaSelection: ActorSelection)(implicit ec: ExecutionContext, t: Timeout) = new RESTAdapter {
    override lazy val store            = storeSelection
    override lazy val metas            = metaSelection
    override lazy val executionContext = ec
    override lazy val timeout          = t
  }
}
