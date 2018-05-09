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

package org.cgc.wfx.impl;

import org.cgc.wfx.Progress;

public class FileUpdateMonitor {

	Progress progress;
	int lastProgress = 0;
	long totalFileSize = 0;
	long exchangedBites = 0;

	/**
	 * @param progress
	 * @param totalFileSize
	 */
	public FileUpdateMonitor(Progress progress, long totalFileSize) {
		this.progress = progress;
		this.totalFileSize = totalFileSize;
	}

	/**
	 * @param updateBitesNo
	 */
	public boolean updateMovedBytes(long updateBitesNo) {
		if (this.progress != null) {
			exchangedBites += updateBitesNo;
			int actualProgress = (int) (exchangedBites * 100 / totalFileSize);
			if (actualProgress > lastProgress) {
				lastProgress = actualProgress;
				return progress.notifyProgress(lastProgress);
			}
		}
		return false;
	}

}
