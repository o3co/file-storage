package jp.o3co.file.store
package json4s

import org.json4s._

object Serializers {
  
  def all = Seq(
    ContentTypeSerializer,
    ResourceIdSerializer,
    ResourceNameSerializer,
    SegmentNameSerializer,
    StatusSerializer
  )


  case object ContentTypeSerializer extends CustomSerializer[ContentType](format => (
    {
      case JString(s) => ContentType(s)
    }, {
      case c: ContentType => JString(c.toString)
    }
  ))

  case object ResourceIdSerializer extends CustomSerializer[ResourceId](format => (
    {
      case JString(s) => ResourceId(java.util.UUID.fromString(s))
    }, {
      case n: ResourceId => JString(n.toString)
    }
  ))

  case object ResourceNameSerializer extends CustomSerializer[ResourceName](format => (
    {
      case JString(s) => ResourceName(s)
    }, {
      case n: ResourceName => JString(n.toString)
    }
  ))

  case object SegmentNameSerializer extends CustomSerializer[SegmentName](format => (
    {
      case JString(s) => SegmentName(s)
    }, {
      case s: SegmentName => JString(s.toString)
    }
  ))


  case object StatusSerializer extends CustomSerializer[Status](format => (
    {
      case JString(s) => Status(s)
    }, {
      case s: Status => JString(s.toString)
    }
  ))
}
