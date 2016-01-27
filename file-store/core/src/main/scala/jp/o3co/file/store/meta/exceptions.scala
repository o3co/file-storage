package jp.o3co.file.store.meta

case class IllegalStateException(meta: Meta, message: String, cause: Throwable = null) extends java.lang.IllegalStateException(message, cause) {
  
  def this(meta: Meta, cause: Throwable) = {
    this(meta, s"Meta ${meta.id} is invalid state.", cause)
  }

  def this(meta: Meta) = {
    this(meta, null)
  }
}
