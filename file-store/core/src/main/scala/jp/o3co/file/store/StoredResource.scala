package jp.o3co.file.store

import java.io.InputStream
import jp.o3co.file.store.meta.Meta

/**
 * Stored resource with meta
 */
case class StoredResource(resource: Resource, meta: Meta) extends Resource {

  /**
   * Convert resource to ByteArray 
   */
  def toBytes = resource.toBytes

  /**
   * Read resource 
   */
  def read(f: InputStream => Unit): Unit = resource.read(f)
}

