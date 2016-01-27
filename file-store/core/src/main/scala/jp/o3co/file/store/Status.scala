package jp.o3co.file.store

/**
 *
 */
sealed abstract class Status(val name: String) {
  /**
   *
   */
  override def toString: String = name
}
object Status {
  def apply(name: String): Status = name.toLowerCase match {
    case "uploading" => Statuses.UPLOADING
    case "uploaded"  => Statuses.UPLOADED
    case "deleted"   => Statuses.DELETED 
    case _           => Statuses.UNKNOWN
  }
}

/**
 *
 */
object Statuses {
  case object UPLOADING extends Status("uploading")
  case object UPLOADED extends Status("uploaded")
  case object DELETED extends Status("deleted")
  case object UNKNOWN extends Status("unknown")
}
