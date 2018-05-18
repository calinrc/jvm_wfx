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


import com.amazonaws.services.s3.model.Bucket
import org.cgc.wfx.FileInformation


case class CosBucketFileInformation(bucket:Bucket) extends FileInformation {

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
    CosFileInformation.FILE_ATTRIBUTE_UNIX_MODE  | CosFileInformation.FILE_ATTRIBUTE_DIRECTORY
  }

  override def getFileCreationTime(): Long = {
    CosFileInformation.javaTimeToFileTime(bucket.getCreationDate.getTime)
  }

  override def getFileLastAccessTime(): Long = {
     fileLastAccessTime
  }

  override def getFileSize(): Long = {
     0
  }

  override def getReserved0(): Long = {
    CosFileInformation.S_IFDIR
  }

  override def getFileName(): String = {
    bucket.getName
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
