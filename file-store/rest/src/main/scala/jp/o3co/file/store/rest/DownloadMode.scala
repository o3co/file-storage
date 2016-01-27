package jp.o3co.file.store
package rest

/**
 * Base class of DownloadMode
 */
sealed class DownloadMode(val name: String) {
  /**
   * Equallity coimparison with other 
   */
  override def equals(other: Any): Boolean = {
    other match {
      case o: DownloadMode => name.equals(o.name)
      case _ => false
    }
  }

  override def toString: String = name
}

object DownloadMode {
  def apply(name: String) = new DownloadMode(name.toLowerCase)
}

/**
 * Default supported DownloadModes
 */
object DownloadModes {
  /**
   * Redirect is a DownloadMode which response 302 Found redirection
   * with stored path.
   */
  case object Redirect extends DownloadMode("redirect")

  /**
   *
   */
  case object Proxy extends DownloadMode("proxy")
}
