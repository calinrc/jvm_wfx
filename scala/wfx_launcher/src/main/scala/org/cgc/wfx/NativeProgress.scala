package org.cgc.wfx

import java.io.File

object NativeProgress {
  def tryLoadingWfxLibrary = {
    val nativeLibPath = new File(System.getProperty("user.home")
      + File.separatorChar + Constants.JVM_WFX_NATIVE_LIBRARY)
    try {
      System.load(nativeLibPath.getAbsolutePath())
    } catch {
      case t:Throwable =>
      System.err
        .println("Fail on loading jvm_wfx.wfx library from path "
          + nativeLibPath.getAbsolutePath())
      t.printStackTrace()
      throw t
    }
    System.out.println("Success on loading jvm_wfx.wfx")
  }
}

class NativeProgress(ptr:Long) extends Progress{

  def notifyProgress(progressVal: Int): Boolean = {
    if (ptr != 0L && ptr != -1L) {
      try {
        notifyProgress(ptr, progressVal)
      } catch{
        case linkError:UnsatisfiedLinkError => {
          System.err
            .println("Fail on calling native method. Try loading native library")
          linkError.printStackTrace()
          NativeProgress.tryLoadingWfxLibrary
          try {
            notifyProgress(ptr, progressVal)
          } catch {
            case t: Throwable => {
              System.err
                .println("Final fail on sending progress notification")
              false
            }
          }
        }
      }
    }else
      false
  }

  @native def notifyProgress(pointer: Long, progressVal: Int): Boolean

}
