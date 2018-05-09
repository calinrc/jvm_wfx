package org.cgc.wfx

trait Progress {
  def notifyProgress(progressVal: Int): Boolean
}
