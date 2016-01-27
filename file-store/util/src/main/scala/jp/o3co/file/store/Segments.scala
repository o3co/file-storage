package jp.o3co.file.store

import akka.actor.ActorRefFactory
import com.typesafe.config._
import jp.o3co.config.ConfigurationException
import scala.collection.JavaConverters._
//
import Implicits._

/**
 *
 */
class Segments(protected val underlying: Map[SegmentName, Segment]) extends Map[SegmentName, Segment]{
  
  def +[B1 >: Segment](kv: (SegmentName, B1)) = {
    underlying + kv
  }

  def -(key: SegmentName) = new Segments(underlying - key)
  /**
   *
   */
  override def apply(name: SegmentName): SegmentDefinition = {
    underlying(name) match {
      case SegmentAlias(name)   => apply(name) 
      case d: SegmentDefinition => d
    }
  }

  /**
   *
   */
  override def get(name: SegmentName): Option[Segment] = {
    underlying.get(name).flatMap { 
      case SegmentAlias(name)  => get(name)
      case d: SegmentDefinition => Option(d)
    }
  }

  def iterator= underlying.iterator.filter {
    case (_, v:SegmentDefinition) => true
    case _ => false
  }
}

object Segments {
  def apply(config: Config)(implicit actorRefFactory: ActorRefFactory): Segments = {
    new Segments((for {
      (key, value) <- config.root().asScala
    } yield {
      //val key = entry.getKey
      //val value = entry.getValue
      (key: SegmentName, value.valueType match {
        case ConfigValueType.STRING => Segment(value.unwrapped.toString)
        case ConfigValueType.OBJECT => Segment(config.getConfig(key))
        case _ => throw new ConfigurationException("Segment has to be a String for alias of another segment, or an Object to declare segment.")
      })
    }).toMap)
  }
}
