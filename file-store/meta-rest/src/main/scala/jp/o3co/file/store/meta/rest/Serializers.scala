package jp.o3co.file.store
package meta.rest

import org.json4s._


/*
 *
import org.json4s.{Formats, DefaultFormats => Json4sDefaultFormats}

object Serializers {
  def formats: Formats = DefaultFormats ++ all

  def all = List(
      BoxSerializer[ResourceId],
      BoxSerializer[Filename],
      BoxSerializer[ContentType],
      BoxSerializer[Segment]
    )

  class Box[T](protected val underlying: T) {
    def unwrapped: T = underlying
  }

  case class BoxSerializer[A <: Box[_]: ClassTag] extends Serializer[A] {

    private val cTag = implicitly[ClassTag[A]].runtimeClass

    def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), A] = {
      case (TypeInfo(cTag, pType), json) => {
        val t = ptype.getOrElse(throw new Exception())

        cTag.instantiate()
      }
    }
    
    def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
      case value: A => Extraction.decompose(value.unwarpped)
    }
  }


  case object UUIDSerializer extends CustomSerializer[UUID](format => 
    ({
      case JString(s) => UUID.fromString(s)
      case JNull => null
    },
    {
      case x: UUID => JString(x.toString)
    })
  )
  
  case object LocaleSerializer extends CustomSerializer[Locale](format => 
    ({
      case JString(s) => Locale(s)
      case JNull => null
    },
    {
      case x: Locale => JString(x.toString)
    })
  )

  case object OSSerializer extends CustomSerializer[OS](format => 
    ({
      case JString(s) => OS(s)
    },
    {
      case x:OS  => JString(x.toString)
    })
  )
}

trait DefaultFormats extends Json4sDefaultFormats {
  override val strict = true
  override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")


}

object DefaultFormats extends DefaultFormats *
*/

