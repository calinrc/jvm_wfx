/*
 *    CosFileSystem.scala file written and maintained by Calin Cocan
 *    Created on: Oct 05, 2015
 *
 * This work is free: you can redistribute it and/or modify it under the terms of Apache License Version 2.0
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the License for more details.
 * You should have received a copy of the License along with this program. If not, see <http://choosealicense.com/licenses/apache-2.0/>.

 ********************************************************************************************************************* */

package org.cgc.wfx.impl

;

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URL
import java.util.ArrayList
import java.util.List

import org.apache.log4j.Logger
import org.cgc.wfx._
import org.cgc.wfx.exception.WfxJvmException

case class CosFileSystem() extends WfxPair {
  val log = Logger.getLogger(CosFileSystem.getClass)
  log.debug("WfxPair instance if  CosFileSystem");

  /*
   * @return int value translation of WfxErrorCodes
   */
  override def initFS(conf : PairConfig):this.type = {
    log.debug("Initialize FS");
    ???
    this
  }


  /**
    * @param folderPath
    * @return String[]
    */
  override def getFolderContent(folderPath: String): Array[String] = ???

  /**
    * @param parentFolder
    * @param fileName
    * @return FileInformation
    */
  override def getFileInformation(parentFolder: String, fileName: String): FileInformation = ???

  /**
    * @param filePath
    * @return boolean
    */
  override def mkDir(filePath: String): Boolean = ???

  /**
    * @param path
    * @return boolean
    */
  override def deletePath(path: String): Boolean = ???

  /**
    * @param oldPath
    * @param newPath
    * @return boolean
    */
  override def renamePath(oldPath: String, newPath: String): Boolean = ???


  /**
    * @param srcPath
    * @param destPath
    * @return boolean
    */
  override def copyPath(srcPath: String, destPath: String): Boolean = ???

  /**
    * @param remotePath
    * @param localPath
    * @return boolean
    */
  override def getFile(remotePath: String, localPath: String, progress: Progress): Unit = ???

  /**
    * @param localPath
    * @param remotePath
    * @return boolean
    */
  override def putFile(localPath: String, remotePath: String, overwrite: Boolean, progress: Progress): Unit = ???

  /**
    * @param repotePath
    * @return boolean
    */
  override def fileExists(repotePath: String): Boolean = ???

}
