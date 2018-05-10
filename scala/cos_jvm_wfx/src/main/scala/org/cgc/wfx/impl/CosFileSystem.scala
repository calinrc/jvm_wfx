/*
 *    FileSystemProxy.java file written and maintained by Calin Cocan
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
import org.cgc.wfx.Constants
import org.cgc.wfx.FileInformation
import org.cgc.wfx.Progress
import org.cgc.wfx.WfxPair
import org.cgc.wfx.exception.WfxJvmException

case class CosFileSystem() extends WfxPair {
  val log = Logger.getLogger(CosFileSystem.getClass)
  log.debug("WfxPair instance if  CosFileSystem");

  /*
   * @return int value translation of WfxErrorCodes
   */
  def initFS() = {
    log.debug("Initialize FS");
    ???
  }


  /**
    * @param folderPath
    * @return String[]
    */
  def getFolderContent(folderPath: String): Array[String] = ???

  /**
    * @param parentFolder
    * @param fileName
    * @return FileInformation
    */
  def getFileInformation(parentFolder: String, fileName: String): FileInformation = ???

  /**
    * @param filePath
    * @return boolean
    */
  def mkDir(filePath: String): Boolean = ???

  /**
    * @param path
    * @return boolean
    */
  def deletePath(path: String): Boolean = ???

  /**
    * @param oldPath
    * @param newPath
    * @return boolean
    */
  def renamePath(oldPath: String, newPath: String): Boolean = ???


  /**
    * @param srcPath
    * @param destPath
    * @return boolean
    */
  def copyPath(srcPath: String, destPath: String): Boolean = ???

  /**
    * @param remotePath
    * @param localPath
    * @return boolean
    */
  def getFile(remotePath: String, localPath: String, progress: Progress): Unit = ???

  /**
    * @param localPath
    * @param remotePath
    * @return boolean
    */
  def putFile(localPath: String, remotePath: String, overwrite: Boolean, progress: Progress): Unit = ???

  /**
    * @param repotePath
    * @return boolean
    */
  def fileExists(repotePath: String): Boolean = ???

}
