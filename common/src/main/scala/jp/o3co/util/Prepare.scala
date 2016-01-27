package jp.o3co.util

/**
 *
 */
trait Prepare {

  private var _prepared: Boolean = false

  /**
   * Override this to avoid auto-preparing
   */
  def autoPrepare: Boolean = true

  /**
   * Prepare the instance ready.
   *
   * ex) 
   * {{{
   *   lazy val service = {..}
   *   def prepare() {
   *     // Avoid service is not loaded on initialization 
   *     service
   *   }
   * }}}
   */
  def prepare: Unit = { _prepared = true }

  def isPrepared: Boolean = _prepared

  if(autoPrepare) prepare 
}
