package org.cgc.wfx

trait WfxPair {

  def initFS(conf : PairConfig): this.type

  /**
    * @param folderPath
    * @return String[]
    */
  def getFolderContent(folderPath: String): Array[String]

  /**
    * @param parentFolder
    * @param fileName
    * @return FileInformation
    */
  def getFileInformation(parentFolder: String, fileName: String): FileInformation

  /**
    * @param filePath
    * @return boolean
    */
  def mkDir(filePath: String): Boolean

  /**
    * @param path
    * @return boolean
    */
  def deletePath(path: String): Boolean

  /**
    * @param oldPath
    * @param newPath
    * @return boolean
    */
  def renamePath(oldPath: String, newPath: String): Boolean


  /**
    * @param srcPath
    * @param destPath
    * @return boolean
    */
  def copyPath(srcPath: String, destPath: String): Boolean

  /**
    * @param remotePath
    * @param localPath
    * @return boolean
    */
  def getFile(remotePath: String, localPath: String, progress: Progress): Unit

  /**
    * @param localPath
    * @param remotePath
    * @return boolean
    */
  def putFile(localPath: String, remotePath: String, overwrite: Boolean, progress: Progress): Unit

  /**
    * @param repotePath
    * @return boolean
    */
  def fileExists(repotePath: String): Boolean
}
