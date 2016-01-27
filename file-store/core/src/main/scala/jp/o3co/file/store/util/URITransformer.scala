package jp.o3co.file.store
package util

import java.net.URI

/***
 * URI transformer
 *
 */
trait URITransformer {

  /**
   *
   */
  def isSupported(uri: URI): Boolean

  /**
   * API
   */
  def transform(uri: URI): URI
}

/**
 * Collection of URITransformer
 * Transform URI with first matched transformer 
 */
case class URITransformers(val transformers: Seq[URITransformer]) extends URITransformer {
  
  def isSupported(uri: URI): Boolean = transformers
    .collectFirst { 
      case t if t.isSupported(uri) => true
    }
    .getOrElse(false)

  /**
   * transform uri to  
   */
  def transform(uri: URI): URI = transformers
    .collectFirst { 
      case t if t.isSupported(uri) => transform(uri)
    }
    .getOrElse(uri)
}

class MatchedURITransformer(matcher: URIMatcher, baseUri: URI) extends URITransformer{

  def isSupported(uri: URI): Boolean = matcher.matches(uri)

  def transform(uri: URI): URI = {
    matcher.matches(uri) match {
      case URIMatcher.Matched(matched, remains) => baseUri.resolve(remains)
      case URIMatcher.Unmatched => throw new Exception("Not matched") 
    }
  }
}


object MatchedURITransformer {
  /**
   *
   */
  def apply(matcher: URIMatcher, baseUri: URI) = new MatchedURITransformer(matcher, baseUri)
}
