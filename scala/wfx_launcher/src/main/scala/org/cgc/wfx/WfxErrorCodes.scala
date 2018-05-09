package org.cgc.wfx

trait WfxErrorCodes {
  def intCode: Int
}

case object OK extends WfxErrorCodes {
  override def intCode = 0
}

case object InitializationError extends WfxErrorCodes {
  override def intCode = 1
}

case object FileEnumerationError extends WfxErrorCodes {
  override def intCode = 2
}

case object ReadError extends WfxErrorCodes {
  override def intCode = 3
}

case object WriteError extends WfxErrorCodes {
  override def intCode = 4
}

case object CloseError extends WfxErrorCodes {
  override def intCode = 5
}

case object MoveError extends WfxErrorCodes {
  override def intCode = 6
}

