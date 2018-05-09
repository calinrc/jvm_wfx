package org.cgc.wfx.exception

final case class WfxJvmException(message:String, cause:Throwable= None.orNull) extends RuntimeException(message, cause){
  def this() = this("", null)

  def this(message: String) = this(message, null)

  def this(cause : Throwable) = this("", cause)
}
