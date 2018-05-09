package org.cgc.wfx

import org.cgc.wfx.exception.WfxJvmException

object FSClientLauncher {
  private val PAIR_CLASS = "org.cgc.wfx.impl.FileSystemProxy"

  def getPairInstance(dependencyFolder: String): WfxPair = {
    try {
      System.out.println("Try getting WfxPair instance")
      val loader = new DepsLoader(dependencyFolder)
      val cl = loader.loadFolder
      Thread.currentThread.setContextClassLoader(cl)
      val pairClasss = cl.loadClass(PAIR_CLASS)
      val obj = pairClasss.newInstance
      if (obj.isInstanceOf[WfxPair]) {
        System.out.println("WfxPair instance obtained")
        obj.asInstanceOf[WfxPair]
      }else{
        System.err.println(s"Unable to initialize the new class loader with jvm dependencies. obj is ${obj}")
        throw new WfxJvmException("Unable to initialize the new class loader with jvm dependencies")
      }
    } catch {
      case wfxEX: WfxJvmException => throw wfxEX
      case cnfEx: ClassNotFoundException =>
        cnfEx.printStackTrace()
        System.err.println("Unable to find WfxPair class " + cnfEx.getMessage)
        throw new WfxJvmException(cnfEx)
      case illAcEx: IllegalAccessException =>
        illAcEx.printStackTrace()
        System.err.println("Fail on accessing WfxPair implementation with message" + illAcEx.getMessage)
        throw new WfxJvmException(illAcEx)
      case instEx: InstantiationException =>
        instEx.printStackTrace()
        System.err.println("Fail on instantiate WfxPair object with message" + instEx.getMessage)
        throw new WfxJvmException(instEx)
      case thr: Throwable =>
        thr.printStackTrace()
        System.err.println("Fail on initialization JVM client with message" + thr.getMessage)
        throw new WfxJvmException(thr)
    }

  }

}
