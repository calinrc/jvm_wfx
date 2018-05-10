/*
 *    IOUtils.java file written and maintained by Calin Cocan
 *    Created on: Oct 16, 2015
 *
 * This work is free: you can redistribute it and/or modify it under the terms of Apache License Version 2.0
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the License for more details.
 * You should have received a copy of the License along with this program. If not, see <http://choosealicense.com/licenses/apache-2.0/>.

 ********************************************************************************************************************* */

package org.cgc.wfx.impl


import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream

object IOUtils {

  def deplete(is: InputStream, os: OutputStream, monitor: FileUpdateMonitor): Boolean = {
    try {
      val buff = Array.fill[Byte](2 * 1024 * 1024){0}
      var cancel:Boolean = false
      var readBytes = is.read(buff)
      while ((readBytes > -1) && !cancel) {
        os.write(buff, 0, readBytes)
        cancel = monitor.updateMovedBytes(readBytes)
        readBytes = is.read(buff)
      }
      cancel
    } finally {
      close(is, os)
    }

  }

  def close(cls: Closeable*): Unit = {
    cls.map(item => {
      try {
        if (item != null) {
          item.close()
        }
      } catch {
        case ex: Exception => //do nothing
      }
    })
  }

}
