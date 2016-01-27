package jp.o3co.file.store
package storage

import akka.actor.{Actor, ActorRef, Props, ActorRefFactory, ActorLogging}
import akka.pattern.pipe
import com.typesafe.config.Config
import java.net.URI
import scala.concurrent.{ExecutionContext, Future}

/**
 * FileStorage is an Actor which purely access DataStore to load or save the resource.
 * 
 *
 */
trait FileStorage extends Actor with ActorLogging with Implicits {

  override def receive = receiveStorage.orElse(receiveInternal)

  implicit def executionContext: ExecutionContext = context.dispatcher 

  /*
   */
  def receiveStorage: Receive = {
    case FileStorage.ResolveLink(path)    =>
      resolveLink(path)
        .map(link => FileStorage.ResolveLinkResult(link))
        .pipeTo(sender)
    case FileStorage.LoadResource(path)   => 
      loadResource(path)
        .map(resource => FileStorage.LoadResourceResult(resource))
        .pipeTo(sender)
    case FileStorage.SaveResource(resource, path)  =>
      saveResource(resource, path)
        .map(link => FileStorage.SaveResourceResult(link))
        .pipeTo(sender)
    //case FileStorage.DeleteResource(path) => 
    //  deleteResource(path).map { 
    //    ret => DeleteResouceResult(Option)
    //  } pipeTo sender
  }

  def resolveLink(path: RelativePath): Future[Option[Link]]

  /**
   * load resource from given path
   */
  def loadResource(path: RelativePath): Future[Option[Resource]]

  /**
   * save resource to given path
   */
  def saveResource(resource: Resource, path: RelativePath): Future[Option[Link]]

  /**
   * Deleted resource 
   */
  //def deleteResource(path: RelativePath): Future[Unit]

  /**
   *
   */
  def receiveInternal: Receive = Actor.emptyBehavior
}

object FileStorage {

  /**
   * Boot FileStorage with the given configuration
   */
  def apply(config: Config)(implicit actorRefFactory: ActorRefFactory): ActorRef = {
    actorRefFactory.actorOf(Props(Class.forName(config.getString("class")), config))
  }

  /**
   * Request to resolve the Public Link from relative path 
   * @param path Relative path of the storage
   */
  case class ResolveLink(path: RelativePath)

  /**
   *
   * @param link Resolved public link
   */
  case class ResolveLinkResult(link: Option[Link])

  case class LoadResource(path: RelativePath)
  case class LoadResourceResult(resource: Option[Resource])

  case class SaveResource(resource: Resource, path: RelativePath)

  /**
   * Response of SaveResource request.
   *
   * @param link if the saved resource is public accessable, then some link. Otherwise, none
   */
  case class SaveResourceResult(link: Option[Link])

  case class DeleteResource(path: RelativePath)
  case class DeleteResourceResult(deletedAt: URI)
}

