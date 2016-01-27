package jp.o3co.file.store

import org.json4s.{Formats => Json4sFormats, DefaultFormats => Json4sDefaultFormats}

package object json4s {
  
  val DefaultFormats: Json4sFormats = Json4sDefaultFormats ++ Serializers.all
}
