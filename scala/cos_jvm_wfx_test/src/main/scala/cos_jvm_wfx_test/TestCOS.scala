/*
 *    TestCOS.scala file written and maintained by Calin Cocan
 *    Created on: Oct 05, 2015
 *
 * This work is free: you can redistribute it and/or modify it under the terms of Apache License Version 2.0
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the License for more details.
 * You should have received a copy of the License along with this program. If not, see <http://choosealicense.com/licenses/apache-2.0/>.

 ********************************************************************************************************************* */

package cos_jvm_wfx_test;

import java.io.File;

import org.cgc.wfx.Constants;
import org.cgc.wfx.FSClientLauncher;
import org.cgc.wfx.FileInformation;
import org.cgc.wfx.WfxPair;

object TestCOS extends App{

		try {
			val homePath = System.getProperty("user.home");

			val pair = FSClientLauncher
					.getPairInstance(homePath + File.separatorChar + Constants.DEPENDENCIES_PATH);
			System.out.println("Init");
			pair.initFS();
			String[] content = pair.getFolderContent("/");
			for (String item : content) {
				System.out.println("\t" + item);
				FileInformation fi = pair.getFileInformation("/", item);
				System.out.println("File " + fi);
			}

			long currentTimeMillis = System.currentTimeMillis();
			String newFolderPath = "/user/newFolder_" + currentTimeMillis;
			System.out.println("Try to create folder 1 " + newFolderPath);
			pair.mkDir(newFolderPath);

			pair.deletePath(newFolderPath);

			String newFolderWithoutParent = "/user/newFolderUnexist_" + (currentTimeMillis + 1) + "/folder2_"
					+ (currentTimeMillis + 2);
			System.out.println("Try to create folder2 " + newFolderWithoutParent);
			pair.mkDir(newFolderWithoutParent);

			pair.deletePath("/user/newFolderUnexist_" + (currentTimeMillis + 1));

			pair.mkDir("/user/folderToRename");
			pair.renamePath("/user/folderToRename", "/user/folderToRenameChanged_" + System.currentTimeMillis());

			pair.mkDir("/user/upload");
			pair.putFile(homePath + File.separatorChar + ".config/jvm_wfx/java/log4j.xml",
					"/user/upload/log4j.xml", true, null);

			pair.getFile("/user/upload/log4j.xml", new File(".").getAbsolutePath() + File.separatorChar + ".cucu", null);

			Runtime runtime = Runtime.getRuntime();

			final int MB = 1024 * 1024;

			System.out.println("Free Memory: " + (runtime.freeMemory() / MB));
			System.out.println("Max Memory: " + (runtime.maxMemory() / MB));
			System.out.println("Total Memory: " + (runtime.totalMemory() / MB));
			System.out.println("Used Memory: " + ((runtime.totalMemory() - runtime.freeMemory()) / MB));

			System.out.println("End");

		} catch (Throwable thr) {
			thr.printStackTrace();
		}


}
