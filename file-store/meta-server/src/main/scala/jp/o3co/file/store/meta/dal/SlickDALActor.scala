package jp.o3co.file.store
package meta 
package dal

import akka.actor.Actor
import akka.pattern.pipe
import scala.concurrent.{ExecutionContext, Future}

trait SlickDALActor extends Actor {
  this: impl.SlickDriver => 
 
  implicit def executionContext: ExecutionContext = context.dispatcher

  def receive = receiveEvent

  def receiveEvent: Receive = {
    case ContainsEntity(id) => 
      contains(id)
        .map {
          case c => ContainsEntityResult(c)
        } pipeTo sender
    case PutEntity(entity) =>
      put(entity)
        .map {
          case e => PutEntityResult()
        } pipeTo sender
    case GetMeta(id, includeRelatives) => 
      get(id)
        .flatMap { meta => 
          meta
            .map { m => 
              if(includeRelatives) relatives(m.id).map { rs => 
                GetMetaSuccess(Option(m), Option(rs))
              } 
              else Future(GetMetaSuccess(Option(m), None))
            }
            .getOrElse(Future(GetMetaSuccess(None, None)))
        }
        .recover {
          case ex: Throwable => GetMetaFailure(ex)
        }
        .pipeTo(sender)
    case event: UpdateMeta => 
      update(event)
        .map { ret => 
          UpdateMetaSuccess(ret)
        }
        .recover {
          case ex: Throwable => DeleteMetaFailure(ex)
        }
        .pipeTo(sender)
    case event: BatchUpdateMeta => 
      update(event)
        .map { ret => 
          UpdateMetaSuccess(ret)
        }
        .recover {
          case ex: Throwable => DeleteMetaFailure(ex)
        }
        .pipeTo(sender)
    case DeleteMeta(id) => 
      delete(id)
        .map {ret => 
          DeleteMetaSuccess(ret)
        }
        .recover {
          case ex: Throwable => DeleteMetaFailure(ex)
        }
        .pipeTo(sender)
  }
}
