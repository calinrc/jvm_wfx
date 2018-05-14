package org.cgc.wfx

import org.cgc.wfx.FSClientLauncher.loadConf
import org.cgc.wfx.exception.WfxJvmException

import scala.io.Source

object FSClientLauncher {
  private val PAIR_CLASS = "org.cgc.wfx.impl.CosFileSystem"

  /**
    *
    * @param dependencyFolder
    * @param pairsConfPath
    * @return WfxPair
    */
  def getPairInstance(dependencyFolder: String, pairsConfPath:String): WfxPair = {
    try {
      System.out.println("Try getting WfxPair instance")
      val loader = new DepsLoader(dependencyFolder)
      val cl = loader.loadFolder
      val wfxConfs = loadConf(pairsConfPath)
      Thread.currentThread.setContextClassLoader(cl)
      val config = wfxConfs.configurations.head
      val pairClasss = cl.loadClass(config.className.getOrElse(PAIR_CLASS))
      val obj = pairClasss.newInstance
      if (obj.isInstanceOf[WfxPair]) {
        System.out.println("WfxPair instance obtained")
        obj.asInstanceOf[WfxPair].initFS(config)
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
  private def loadConf(confPath:String):WfxPairsConfigurations = {
    import spray.json._
    Source.fromFile(confPath).getLines.mkString.parseJson.convertTo[WfxPairsConfigurations]
  }

}
