package org.cgc.wfx

import java.io.File
import java.net.MalformedURLException
import java.net.URLClassLoader
import java.net.URL

import org.cgc.wfx.exception.WfxJvmException

class DepsLoader(val folderPath: String) {


  def loadFolder: ClassLoader = {
    try {
      val f = new File(folderPath)
      val urls: Array[URL] = if (f.exists() && f.isDirectory()) {
        f.listFiles().filter(!_.isDirectory).map {
          item => {
            val url = item.toURI().toURL()
            println(s"Load file ${url}")
            url
          }
        }
      } else {
        Array(new File(folderPath).toURI().toURL())
      }

      URLClassLoader.newInstance(urls, getClass.getClassLoader)
    }
    catch {
      case ex: MalformedURLException => ex.printStackTrace()
        throw new WfxJvmException(ex)
    }
  }
}
