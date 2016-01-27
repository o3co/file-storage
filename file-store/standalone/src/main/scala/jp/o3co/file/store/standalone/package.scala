package jp.o3co.file.store

import org.json4s.{DefaultFormats => Json4sDefaultFormats}
import jp.o3co.file.store.json4s.Serializers

package object standalone {

  /**
   *
   */
  val DefaultFormats = Json4sDefaultFormats ++ Serializers.all
}
