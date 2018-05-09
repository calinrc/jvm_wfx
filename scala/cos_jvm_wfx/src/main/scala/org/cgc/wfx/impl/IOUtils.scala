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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

object IOUtils {

	def deplate(InputStream is, OutputStream os,
			FileUpdateMonitor monitor):Boolean {
		try {
			byte[] buff = new byte[2 * 1024 * 1024];
			int readBytes = 0;

			boolean cancel = false;
			while ((readBytes = is.read(buff)) > -1 && !cancel) {
				os.write(buff, 0, readBytes);
				cancel = monitor.updateMovedBytes(readBytes);
			}
			return cancel;
		} finally {
			close(is, os);
		}

	}

	def close(Closeable... cls):Unit {
		for (Closeable item : cls) {
			try {
				if (item != null) {
					item.close();
				}
			} catch (Exception ex) {

			}
		}
	}

}
