package jp.o3co.file.store

import scala.language.implicitConversions
import spray.http.{MediaType, MediaTypes}

/**
 *
 */
class ContentType(val mainType: String, val subType: String, val charset: Option[String] = None) {
  /**
   *
   */
  override val toString: String = 
    if(charset.isDefined) "%s/%s; charset = %s".format(mainType, subType, charset.get)
    else "%s/%s".format(mainType, subType)

  def toFileExtension: String = ""

  def toMediaType: MediaType = 
    MediaTypes.getForKey((mainType, subType)).getOrElse(MediaType.custom(mainType, subType))
}

/**
 *
 */
object ContentType {

  /**
   *
   */
  def apply(mainType: String, subType: String, charset: Option[String] = None) = new ContentType(mainType, subType, charset)

  /**
   *
   */
  implicit def apply(literal: String): ContentType = {
    val parts = literal.split(';')
    val mediaType = parts.head
    val params    = parts.tail

    val (mainType, subType) = parseMediaType(mediaType)
    val charset = parseParams(params).get("charset")

    new ContentType(mainType, subType, charset)
  }

  /**
   *
   */
  def parseMediaType(typeLiteral: String) = {
    typeLiteral.split('/') match {
      case Array(t1, t2, _ @ _*) => (t1, t2)
      case _ => throw new Exception(s""""$typeLiteral" is invalid format of media type.""")
    }
  }

  /**
   *
   */
  def parseParams(params: Seq[String]): Map[String, String] = {
    params
      .map { p => 
        (p.split('=') match {
          case Array(key, value, _ @ _*) => Some((key, value))
          case _ => None
        })
      }
      .collect {
        case Some(kv) => kv
      }
      .toMap
  }
}

