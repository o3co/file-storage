package jp.o3co.config

import com.typesafe.config.Config
import scala.concurrent.duration._
import scala.reflect.runtime.universe._
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.language.implicitConversions

trait Implicits { 
  /**
   *
   */
  implicit class ConfigOperator(val underlying: Config) {

    def get[T: WeakTypeTag](path: String): T = {
      (weakTypeOf[T] match {
        case t if t =:= typeOf[FiniteDuration] => FiniteDuration(underlying.getDuration(path, MILLISECONDS), MILLISECONDS)
        case t if t =:= typeOf[Duration]       => Duration(underlying.getDuration(path, MILLISECONDS), MILLISECONDS)
        case t if t =:= typeOf[List[Boolean]]  => underlying.getBooleanList(path).asScala.toList
        case t if t =:= typeOf[List[Number]]   => underlying.getNumberList(path).asScala.toList
        case t if t =:= typeOf[List[Int]]      => underlying.getIntList(path).asScala.toList
        case t if t =:= typeOf[List[Long]]     => underlying.getLongList(path).asScala.toList
        case t if t =:= typeOf[List[Double]]   => underlying.getDoubleList(path).asScala.toList
        case t if t =:= typeOf[List[String]]   => underlying.getStringList(path).asScala.toList
        case t if t =:= typeOf[String]         => underlying.getString(path)
        case t if t =:= typeOf[Number]         => underlying.getNumber(path)
        case t if t =:= typeOf[Int]      => underlying.getInt(path)
        case t if t =:= typeOf[Long]     => underlying.getLong(path)
        case t if t =:= typeOf[Double]   => underlying.getDouble(path)
        case t if t =:= typeOf[Boolean]  => underlying.getBoolean(path)
        case t => throw new RuntimeException(s"""Type "${t}" is not supported to get.""")
      })
        .asInstanceOf[T]
        //case t if t =:= typeOf[Float] => underlying.getFloat(path)
    }

    def getOption[T: WeakTypeTag](path: String) = {
      if(underlying.hasPath(path)) Some(get[T](path))
      else None
    }

    /**
     * {{{
     *   config.getOrElse("path", default)
     *   // or 
     *   config.getOrElse("path", {
     *     default
     *   })
     * }}}
     */
    def getOrElse[T: WeakTypeTag](path: String, default: => T): T = 
      getOption[T](path).getOrElse(default)

    def getFiniteDuration(path: String): FiniteDuration = 
      FiniteDuration(underlying.getDuration(path, MILLISECONDS), MILLISECONDS)
  }
}

object Implicits extends Implicits 
