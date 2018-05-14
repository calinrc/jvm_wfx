/*
 *    CosFileSystem.scala file written and maintained by Calin Cocan
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

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import org.apache.log4j.Logger
import org.cgc.wfx._

import scala.collection.JavaConversions._
import scala.util.Try

case class CosFileSystem(val cosOpt: Option[AmazonS3Client] = None) extends WfxPair {
  val log = Logger.getLogger(CosFileSystem.getClass)
  log.debug("WfxPair instance of CosFileSystem");

  /*
   * @return int value translation of WfxErrorCodes
   */
  override def initFS(conf: PairConfig): WfxPair = {
    log.debug("Initialize FS");
    val endpointUrl = conf.properties.get(CosConstants.ENDPOINT_URL)
    val user_id = conf.properties.get(CosConstants.ENDPOINT_URL)
    val password = conf.properties.get(CosConstants.ENDPOINT_URL)
    val cos = new AmazonS3Client(new BasicAWSCredentials(user_id, password))
    cos.setEndpoint(endpointUrl)
    new CosFileSystem(Some(cos))
  }


  /**
    * @param folderPath
    * @return String[]
    */
  override def getFolderContent(folderPath: String): Array[String] = {
    val folders = folderPath.split(File.separatorChar).toList
    cosOpt.map { cos =>
      folders match {
        case Nil => cos.listBuckets().map(_.getName).toArray
        case head :: Nil => cos.listObjects(head).getObjectSummaries.map(_.getKey).toArray
        case head :: rest => cos.listObjects(head, rest.mkString("/")).getObjectSummaries.map(_.getKey).toArray
      }
    }.getOrElse(Array[String]())
  }

  /**
    * @param parentFolder
    * @param fileName
    * @return FileInformation
    */
  override def getFileInformation(parentFolder: String, fileName: String): FileInformation = {
    val folders = parentFolder.split(File.separatorChar).toList
    cosOpt.map { cos =>
      folders match {
        case Nil => CosBucketFileInformation(cos.listBuckets().filter(_.getName.equals(fileName)).head)
        case head :: Nil => CosFileInformation(cos.getObject(head, fileName))
        case head :: rest => CosFileInformation(cos.getObject(head, rest.mkString("/") + fileName))
      }
    }.get
  }

  /**
    * @param filePath
    * @return boolean
    */
  override def mkDir(filePath: String): Boolean = {
    val folders = filePath.split(File.separatorChar).toList
    cosOpt.map(cos =>
      folders match {
        case Nil => /*do nothing */ true
        case head :: Nil => Try {
          cos.createBucket(head)
        }.toOption.map(_ => true).getOrElse(false)
        case head :: tail => false ////sdas Try{cos..createBucket(head)}.toOption.map(_=> true).getOrElse(false)
      }).getOrElse(false)
  }

  /**
    * @param path
    * @return boolean
    */
  override def deletePath(path: String): Boolean = {
    cosOpt.map(cos => cos.deleteBucket(path)) match {
      case Some(_) => true
      case None => false
    }
  }

  /**
    * @param oldPath
    * @param newPath
    * @return boolean
    */
  override def renamePath(oldPath: String, newPath: String): Boolean = {
    false
  }


  /**
    * @param srcPath
    * @param destPath
    * @return boolean
    */
  override def copyPath(srcPath: String, destPath: String): Boolean = {
    false
  }

  /**
    * @param remotePath
    * @param localPath
    * @return boolean
    */
  override def getFile(remotePath: String, localPath: String, progress: Progress): Unit = {

  }

  /**
    * @param localPath
    * @param remotePath
    * @return boolean
    */
  override def putFile(localPath: String, remotePath: String, overwrite: Boolean, progress: Progress): Unit = {

  }

  /**
    * @param repotePath
    * @return boolean
    */
  override def fileExists(repotePath: String): Boolean = {
    cosOpt.map(cos => cos.doesBucketExist(repotePath)) match {
      case Some(exist) => exist
      case None => false
    }
  }

}
