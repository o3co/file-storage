package jp.o3co.file.store.util

import java.net.URI
import scala.util.matching.Regex
import scala.language.implicitConversions

/**
 *
 */
trait URIMatcher {
  /**
   *
   */
  def matches(uri: URI): URIMatcher.Matching
}

object URIMatcher {

  sealed trait Matching

  case class Matched(matching: String, remains: String) extends Matching {
  }
  case object Unmatched extends Matching
  
  implicit def matchingToBoolean(result: Matching): Boolean = {
    result match {
      case Matched(_, _) => true
      case Unmatched     => false
    }
  }
}

object URIMatchers {
  /**
   *
   */
  class PrefixMatcher(val pattern: Regex) extends URIMatcher {

    def matches(uri: URI) = {
      pattern.findPrefixMatchOf(uri.toString) match {
        case Some(matching) => URIMatcher.Matched(matching.matched, matching.after.toString)
        case None => URIMatcher.Unmatched
      }
    }
  }
  
  object PrefixMatcher {
    def apply(prefix: String) = new PrefixMatcher(prefix.r)

    def apply(prefix: Regex) = new PrefixMatcher(prefix)
  }
}
