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

package org.cgc.wfx.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.cgc.wfx.Constants;
import org.cgc.wfx.FileInformation;
import org.cgc.wfx.Progress;
import org.cgc.wfx.WfxPair;
import org.cgc.wfx.exception.WfxHdfsException;

public class FileSystemProxy implements WfxPair {
	private static final Logger log = Logger.getLogger(FileSystemProxy.class);
	private FileSystem fileSystem;

	public FileSystemProxy() {
		log.debug("WfxPair instance if  FileSystemProxy");
	}

	/**
	 * @return int value translation of WfxErrorCodes
	 */
	public void initFS() {
		log.debug("Initialize FS");
		try {
			Configuration config = new Configuration();

			for (String file : new String[] { "core-site.xml",
					"mapred-site.xml", "hdfs-site.xml", "yarn-site.xml" }) {
				URL url = new File(System.getProperty("user.home")
						+ File.separatorChar + Constants.DEPENDENCIES_PATH
						+ File.separatorChar + file).toURI().toURL();
				if (url != null) {
					config.addResource(url);
				}

			}
			FileSystem fileSystem = FileSystem.get(config);
			this.fileSystem = fileSystem;
		} catch (Throwable ioEx) {
			log.info("Unable to create HDFS file system", ioEx);
			throw new WfxHdfsException(ioEx);
		} finally {
			log.debug("End Initialize FS");
		}
	}

	/**
	 * @param folderPath
	 * @return String[]
	 */
	public String[] getFolderContent(String folderPath) {
		try {
			log.debug("Try getting folder content for " + folderPath);
			Path fPath = new Path(folderPath);
			if (this.fileSystem.isDirectory(fPath)) {
				List<String> contentList = new ArrayList<String>();
				FileStatus[] fstatuses = this.fileSystem.listStatus(fPath);
				for (FileStatus status : fstatuses) {
					contentList.add(status.getPath().getName());
				}
				log.debug("Folder content is : " + contentList);
				return contentList.toArray(new String[contentList.size()]);
			} else {
				return new String[0];
			}

		} catch (IOException ioEx) {
			log.error("FAIL on getting folder content for " + folderPath, ioEx);
			throw new WfxHdfsException(ioEx);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cgc.wfx.WfxPair#getFileInformation(java.lang.String,
	 * java.lang.String)
	 */
	public FileInformation getFileInformation(String parentFolder,
			String fileName) {

		StringBuilder sb = new StringBuilder();
		sb.append(parentFolder);
		if (!parentFolder.endsWith("/")) {
			sb.append('/');
		}
		sb.append(fileName);

		Path path = new Path(sb.toString());

		log.debug("Try getting file informations for " + sb.toString());
		try {

			FileStatus fstatus = this.fileSystem.getFileStatus(path);
			FileInformationImpl fileInformationImpl = new FileInformationImpl(
					fstatus);
			log.debug("File details" + fileInformationImpl);
			return fileInformationImpl;
		} catch (IOException ioEx) {
			log.error("FAIL on getting file info for " + parentFolder + "/"
					+ fileName, ioEx);
			throw new WfxHdfsException(ioEx);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cgc.wfx.WfxPair#mkDir(java.lang.String)
	 */
	@Override
	public boolean mkDir(String folderPath) {
		log.debug("Try to create folder " + folderPath);
		Path fPath = new Path(folderPath);
		try {
			return this.fileSystem.mkdirs(fPath);
		} catch (IOException ioEx) {
			log.error("FAIL on creating folder " + fPath, ioEx);
			throw new WfxHdfsException(ioEx);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cgc.wfx.WfxPair#rmDir(java.lang.String)
	 */
	@Override
	public boolean deletePath(String path) {
		log.debug("Try to delete path " + path);
		Path fPath = new Path(path);
		try {
			return this.fileSystem.delete(fPath, true);
		} catch (IOException ioEx) {
			log.error("FAIL on creating folder " + fPath, ioEx);
			throw new WfxHdfsException(ioEx);
		}
	}

	@Override
	public boolean renamePath(String oldPath, String newPath) {
		log.debug("Try to rename path " + oldPath + " to new path " + newPath);
		Path foldPath = new Path(oldPath);
		Path fnewPath = new Path(newPath);
		try {
			return this.fileSystem.rename(foldPath, fnewPath);
		} catch (IOException ioEx) {
			log.error("FAIL on renaming path " + foldPath + " to new path "
					+ fnewPath, ioEx);
			throw new WfxHdfsException(ioEx);
		}
	}

	@Override
	public boolean copyPath(String srcPath, String destPath) {
		log.debug("Try to rename path " + srcPath + " to new path " + destPath);
		Path fsrcPath = new Path(srcPath);
		Path fdestPath = new Path(destPath);
		try {
			return FileUtil.copy(this.fileSystem, fsrcPath, this.fileSystem,
					fdestPath, false, fileSystem.getConf());
		} catch (IOException ioEx) {
			log.error("FAIL on renaming path " + fsrcPath + " to new path "
					+ fdestPath, ioEx);
			throw new WfxHdfsException(ioEx);
		}
	}

	@Override
	public void getFile(String remotePath, String localPath, Progress progress) {
		log.debug("Try to get path " + remotePath + " to local path "
				+ localPath);
		Path fRemotePath = new Path(remotePath);
		InputStream in = null;
		OutputStream out = null;
		try {
			FileStatus fstatus = this.fileSystem.getFileStatus(fRemotePath);
			FileUpdateMonitor fileMonitor = new FileUpdateMonitor(progress,
					fstatus.getLen());
			in = this.fileSystem.open(fRemotePath);
			out = new FileOutputStream(new File(localPath));
			boolean cancel = IOUtils.deplate(in, out, fileMonitor);
			if (cancel) {
				out.close();
				out = null;
				new File(localPath).delete();
			}
		} catch (IOException ioEx) {
			log.error("FAIL on getting path " + fRemotePath + " to local path "
					+ localPath, ioEx);
			throw new WfxHdfsException(ioEx);
		} finally {
			IOUtils.close(in, out);
		}
	}

	@Override
	public void putFile(String localPath, String remotePath, boolean overwrite,
			Progress progress) {
		log.debug("Try to get path " + remotePath + " to local path "
				+ localPath);
		Path fRemotePath = new Path(remotePath);
		InputStream in = null;
		OutputStream out = null;
		try {
			File file = new File(localPath);
			in = new FileInputStream(file);
			FileUpdateMonitor fileMonitor = new FileUpdateMonitor(progress,
					file.length());
			out = this.fileSystem.create(fRemotePath, overwrite);
			boolean cancel = IOUtils.deplate(in, out, fileMonitor);
			if (cancel) {
				out.close();
				out = null;
				this.fileSystem.delete(fRemotePath, true);
			}
		} catch (IOException ioEx) {
			log.error("FAIL on putting path " + fRemotePath + " to local path "
					+ localPath, ioEx);
			throw new WfxHdfsException(ioEx);
		} finally {
			IOUtils.close(in, out);
		}
	}

	@Override
	public boolean fileExists(String remotePath) {
		log.debug("Check  path " + remotePath + " existance");
		Path fRemotePath = new Path(remotePath);
		try {
			boolean retval = this.fileSystem.exists(fRemotePath);
			log.debug("Path " + remotePath
					+ (retval ? " exists " : " not exists"));
			return retval;
		} catch (IOException ioEx) {
			log.error("FAIL on cheking path existance" + fRemotePath, ioEx);
			throw new WfxHdfsException(ioEx);
		}
	}

}
