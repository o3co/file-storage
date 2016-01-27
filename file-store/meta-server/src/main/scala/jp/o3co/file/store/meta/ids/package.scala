package jp.o3co.file.store
package meta

import java.util.UUID

package object ids {

  case object Generate

  case class GenerateResult(value: ResourceId)
}
