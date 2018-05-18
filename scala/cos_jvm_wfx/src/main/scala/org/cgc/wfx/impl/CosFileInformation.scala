/*
 *    IOUtils.java file written and maintained by Calin Cocan
 *    Created on: Oct 16, 2015
 *
 * This work is free: you can redistribute it and/or modify it under the terms of Apache License Version 2.0
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the License for more details.
 * You should have received a copy of the License along with this program. If not, see <http://choosealicense.com/licenses/apache-2.0/>.

 ********************************************************************************************************************* */

package org.cgc.wfx.impl



import com.amazonaws.services.s3.model.S3Object
import org.cgc.wfx.FileInformation

object CosFileInformation {
  val FILE_ATTRIBUTE_UNIX_MODE = 0x80000000L
  val FILE_ATTRIBUTE_DIRECTORY = 0x00000010L

  val S_IFDIR = 16384 // 40000 in octal
  val S_IFLNK = 40960 // 120000 in octal

  val S_IRUSR = 256 //0400 in octal  /* Read by owner. */
  val S_IWUSR = 128  // 0200 in octal  /* Write by owner. */
  val S_IXUSR = 64 //100 in octal  /* Execute by owner. */

  // private static int S_IRGRP = (S_IRUSR >> 3) /* Read by group. */
  // private static int S_IWGRP = (S_IWUSR >> 3) /* Write by group. */
  // private static int S_IXGRP = (S_IXUSR >> 3) /* Execute by group. */
  //
  // private static int S_IROTH = (S_IRGRP >> 3) /* Read by others. */
  // private static int S_IWOTH = (S_IWGRP >> 3) /* Write by others. */
  // private static int S_IXOTH = (S_IXGRP >> 3) /* Execute by others. */

  /**
    * @param javaTime
    *            - in milliseconds since January 1, 1970 UTC.
    * @return fileTime - number of 100-nanosecond intervals since January 1,
    *         1601.
    */
  def javaTimeToFileTime(javaTime: Long): Long = {
    (javaTime * 10000) + 116444736000000000L
  }


  def fsActionToUnixVal(/*some param here*/): Int = {
    var retVal = 0
    //		if (action.implies(FsAction.READ)) {
    //			retVal |= S_IRUSR
    //		}
    //		if (action.implies(FsAction.WRITE)) {
    //			retVal |= S_IWUSR
    //		}
    //		if (action.implies(FsAction.EXECUTE)) {
    //			retVal |= S_IXUSR
    //		}
    retVal
  }

}

case class CosFileInformation(obj:S3Object) extends FileInformation {

  var fileAttributes: Long = 0L
  var fileCreationTime = 0L
  var fileLastAccessTime = 0L
  var fileSize = 0L
  var reserved0 = 0L
  var fileName = ""

  //	public CosFileInformation(FileStatus fstatus) {
  //		boolean isDir = fstatus.isDirectory()
  //		this.fileAttributes |= FILE_ATTRIBUTE_UNIX_MODE
  //		if (isDir) {
  //			this.fileAttributes |= FILE_ATTRIBUTE_DIRECTORY
  //		}
  //
  //		this.fileCreationTime = javaTimeToFileTime(fstatus
  //				.getModificationTime())
  //		this.fileLastAccessTime = javaTimeToFileTime(fstatus.getAccessTime())
  //		if (isDir == false) {
  //			this.fileSize = fstatus.getLen()
  //		} else {
  //			this.fileSize = 0
  //		}
  //		this.reserved0 = 0
  //
  //		if ((this.fileAttributes & FILE_ATTRIBUTE_DIRECTORY) != 0) {
  //			this.reserved0 |= S_IFDIR
  //		}
  //		if (fstatus.isSymlink()) {
  //			this.reserved0 |= S_IFLNK
  //		}
  //		FsPermission fperm = fstatus.getPermission()
  //		this.reserved0 |= fsActionToUnixVal(fperm.getUserAction())
  //		this.reserved0 |= (fsActionToUnixVal(fperm.getGroupAction()) >> 3)
  //		this.reserved0 |= (fsActionToUnixVal(fperm.getOtherAction()) >> 6)
  //
  //		this.fileName = fstatus.getPath().getName()
  //
  //	}


  override def getFileAttributes(): Long = {
    return fileAttributes
  }

  override def getFileCreationTime(): Long = {
    return fileCreationTime
  }

  override def getFileLastAccessTime(): Long = {
    return fileLastAccessTime
  }

  override def getFileSize(): Long = {
    return fileSize
  }

  override def getReserved0(): Long = {
    return reserved0
  }

  override def getFileName(): String = {
    return fileName
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  override def toString: String = {
    s"""
       			 | FileName: ${this.getFileName()}
       			 |	 Size: ${getFileSize()}
       			 |   Attributes: ${getFileAttributes()}
       			 |   Creation Time: ${getFileCreationTime()}
       			 |   Last Access Time: ${getFileLastAccessTime()}
       			 |   Reserved flags: ${getReserved0()}
		""".stripMargin
  }

}
