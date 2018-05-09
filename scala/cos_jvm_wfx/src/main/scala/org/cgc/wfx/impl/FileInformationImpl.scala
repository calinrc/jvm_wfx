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

package org.cgc.wfx.impl;

import org.cgc.wfx.FileInformation

	object FileInformationImpl{
		val FILE_ATTRIBUTE_UNIX_MODE = 0x80000000L
		val FILE_ATTRIBUTE_DIRECTORY = 0x00000010L

		val S_IFDIR = 40000
		val S_IFLNK = 120000

		val _IRUSR = 400 /* Read by owner. */
		val S_IWUSR = 200 /* Write by owner. */
		val S_IXUSR = 100 /* Execute by owner. */

		// private static int S_IRGRP = (S_IRUSR >> 3); /* Read by group. */
		// private static int S_IWGRP = (S_IWUSR >> 3); /* Write by group. */
		// private static int S_IXGRP = (S_IXUSR >> 3); /* Execute by group. */
		//
		// private static int S_IROTH = (S_IRGRP >> 3); /* Read by others. */
		// private static int S_IWOTH = (S_IWGRP >> 3); /* Write by others. */
		// private static int S_IXOTH = (S_IXGRP >> 3); /* Execute by others. */

	}

case class FileInformationImpl extends FileInformation {

	private long fileAttributes;
	private long fileCreationTime;
	private long fileLastAccessTime;
	private long fileSize;
	private long reserved0;
	private String fileName;

	public FileInformationImpl(FileStatus fstatus) {
		boolean isDir = fstatus.isDirectory();
		this.fileAttributes |= FILE_ATTRIBUTE_UNIX_MODE;
		if (isDir) {
			this.fileAttributes |= FILE_ATTRIBUTE_DIRECTORY;
		}

		this.fileCreationTime = javaTimeToFileTime(fstatus
				.getModificationTime());
		this.fileLastAccessTime = javaTimeToFileTime(fstatus.getAccessTime());
		if (isDir == false) {
			this.fileSize = fstatus.getLen();
		} else {
			this.fileSize = 0;
		}
		this.reserved0 = 0;

		if ((this.fileAttributes & FILE_ATTRIBUTE_DIRECTORY) != 0) {
			this.reserved0 |= S_IFDIR;
		}
		if (fstatus.isSymlink()) {
			this.reserved0 |= S_IFLNK;
		}
		FsPermission fperm = fstatus.getPermission();
		this.reserved0 |= fsActionToUnixVal(fperm.getUserAction());
		this.reserved0 |= (fsActionToUnixVal(fperm.getGroupAction()) >> 3);
		this.reserved0 |= (fsActionToUnixVal(fperm.getOtherAction()) >> 6);

		this.fileName = fstatus.getPath().getName();

	}

	/**
	 * @param javaTime
	 *            - in milliseconds since January 1, 1970 UTC.
	 * @return fileTime - number of 100-nanosecond intervals since January 1,
	 *         1601.
	 */
	private static long javaTimeToFileTime(long javaTime) {
		long retVal = (javaTime * 10000) + 116444736000000000L;
		return retVal;
	}

	private static int fsActionToUnixVal(FsAction action) {
		int retVal = 0;
		if (action.implies(FsAction.READ)) {
			retVal |= S_IRUSR;
		}
		if (action.implies(FsAction.WRITE)) {
			retVal |= S_IWUSR;
		}
		if (action.implies(FsAction.EXECUTE)) {
			retVal |= S_IXUSR;
		}
		return retVal;
	}

	public long getFileAttributes() {
		return fileAttributes;
	}

	public long getFileCreationTime() {
		return fileCreationTime;
	}

	public long getFileLastAccessTime() {
		return fileLastAccessTime;
	}

	public long getFileSize() {
		return fileSize;
	}

	public long getReserved0() {
		return reserved0;
	}

	public String getFileName() {
		return fileName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("FileName: ").append(this.getFileName());
		sb.append("\n\t Size:").append(getFileSize());
		sb.append("\n\t Attributes:").append(getFileAttributes());
		sb.append("\n\t Creation Time:").append(getFileCreationTime());
		sb.append("\n\t Last Access Time:").append(getFileLastAccessTime());
		sb.append("\n\t Reserved flags:").append(getReserved0());
		return sb.toString();

	}

}
