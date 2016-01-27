package jp.o3co.file.store
package meta
package rest
  
import spray.routing.PathMatchers

trait Matchers extends PathMatchers {
  val IDSegment = JavaUUID 
}

object Matchers extends Matchers 

