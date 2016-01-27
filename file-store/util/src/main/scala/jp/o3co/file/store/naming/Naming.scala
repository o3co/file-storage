package jp.o3co.file.store.naming

import jp.o3co.file.store.meta.Meta

/**
 * Create filename from specified Meta
 */
trait Naming {
  /**
   * Create name for the meta
   */
  def apply(meta: Meta): String 
}

