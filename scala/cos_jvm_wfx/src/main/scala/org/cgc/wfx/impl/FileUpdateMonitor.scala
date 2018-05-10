/*
 *    FileUpdateMonitor.java file written and maintained by Calin Cocan
 *    Created on: Oct 16, 2015
 *
 * This work is free: you can redistribute it and/or modify it under the terms of Apache License Version 2.0
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the License for more details.
 * You should have received a copy of the License along with this program. If not, see <http://choosealicense.com/licenses/apache-2.0/>.

 ********************************************************************************************************************* */

package org.cgc.wfx.impl


import org.cgc.wfx.Progress

case class FileUpdateMonitor(val progressOpt: Option[Progress], totalFileSize: Long) {

  var lastProgress: Integer = 0
  var exchangedBites = 0L

  /**
    * @param updateBitesNo
    */
  def updateMovedBytes(updateBitesNo: Long): Boolean = {
    progressOpt.map(progress => {
      exchangedBites += updateBitesNo
      val actualProgress = (exchangedBites * 100 / totalFileSize).toInt
      if (actualProgress > lastProgress) {
        lastProgress = actualProgress
        progress.notifyProgress(lastProgress)
      } else
        false
    }).getOrElse(false)
  }

}
