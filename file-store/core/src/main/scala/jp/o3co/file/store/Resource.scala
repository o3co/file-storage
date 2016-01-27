package jp.o3co.file.store

import java.net.URL
import java.io.File
import java.io.{InputStream, OutputStream}
import java.io.{FileInputStream, FileOutputStream}
import java.io.ByteArrayInputStream
import org.apache.commons.io.FileUtils

trait Resource {
  /**
   * Convert Resource to ByteArray
   */
  def toBytes: Array[Byte] 

  /**
   * Read Resource with InputStream 
   */
  def read(f: InputStream => Unit): Unit
}

trait WritableResource extends Resource {
  /**
   * Write resource with OutputStream
   */
  def write(f: OutputStream => Unit): Unit
}

/**
 * ByteArrayResource is a type of resource represent with the ByteArray 
 */
case class ByteArrayResource(bytes: Array[Byte]) extends Resource {
  /**
   * {@inheritDoc}
   */
  def toBytes = bytes

  /**
   * {@inheritDoc}
   */
  def read(f: InputStream => Unit) = {
    val stream = new ByteArrayInputStream(bytes)
    try {
      f(stream)
    } finally {
      stream.close
    }
  }
}

/**
 *
 */
trait FileResource extends Resource {

  /**
   * File Resource
   */
  def file: File 

  /**
   * {@inheritDoc}
   */
  def read(f: InputStream => Unit) = {
    val stream = new FileInputStream(file)
    try {
      f(stream)
    } finally {
      stream.close
    }
  }

  /**
   * Write resource with OutputStream 
   */
  def write(f: OutputStream => Unit) = {
    val stream = new FileOutputStream(file)
    try {
      f(stream)
    } finally {
      stream.close
    }
  }

  def copy(url: URL) = FileUtils.copyURLToFile(url, file)

  def write(bytes: Array[Byte]): Unit = {
    FileUtils.writeByteArrayToFile(file, bytes)
  }

  def toBytes = FileUtils.readFileToByteArray(file)
}

/**
 *
 */
class LocalFileResource(override val file: File) extends FileResource {
  if(!file.exists()) {
    throw new java.io.FileNotFoundException(s"""File "$file" is not exists.""")
  }
}

/**
 *
 */
object LocalFileResource {
  /**
   *
   */
  def apply(file: File) = new LocalFileResource(file)

  /**
   *
   */
  def apply(filepath: String) = new LocalFileResource(new File(filepath))
}

/**
 * TemporaryFileResource is a type of FileResource which use TempFile as its internal resource
 */
class TemporaryFileResource extends FileResource {
  /**
   * {@inheritDoc}
   */
  val file = {
    val f = File.createTempFile("resource_", ".tmp")
    // Label delete when system exits
    f.deleteOnExit()
    f
  }
}

object TemporaryFileResource {

  /**
   * Factory method with ByteArray
   */
  def apply(content: Array[Byte]) = {
    val resource = new TemporaryFileResource()
    resource.write(content)
    resource
  }
}

case class WebResource(url: URL) extends Resource {

  /**
   * Load resource when firsttime access.
   */
  lazy val internal = new TemporaryFileResource {
    // Copy URL contents to file when created
    FileUtils.copyURLToFile(url, file)
  }

  def toBytes = internal.toBytes

  def read(f: InputStream => Unit) = internal.read(f)
}
