package org.cgc.wfx

trait FileInformation {
  /**
    * @return file attributes
    */
  def getFileAttributes: Long

  /**
    * @return file creation time
    */
  def getFileCreationTime: Long

  /**
    * @return last file access time
    */
  def getFileLastAccessTime: Long

  /**
    * @return file size
    */
  def getFileSize: Long

  /**
    * @return reserved file flags
    */
  def getReserved0: Long

  /**
    * @return fine name
    */
  def getFileName: String
}
