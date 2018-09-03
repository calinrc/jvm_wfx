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

import java.io.{ByteArrayInputStream, File}

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.PutObjectRequest
import org.apache.log4j.Logger
import org.cgc.wfx._

import scala.collection.JavaConversions._
import com.amazonaws.services.s3.model.ObjectMetadata


object CosFileSystem {
  val metadata = new ObjectMetadata
  metadata.setContentLength(0)
  // create empty content
  val emptyContent = new ByteArrayInputStream(new Array[Byte](0))
}

case class CosFileSystem(val cosOpt: Option[AmazonS3Client] = None) extends WfxPair {
  val log = Logger.getLogger(this.getClass)
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

    def createFolderIfDoesNotExist(part: String, parentBucket: Option[String]): Option[Boolean] = {
      cosOpt.map(cos =>
        parentBucket match {
          case None => {
            if (!cos.doesBucketExist(part)) {
              cos.createBucket(part)
              true
            } else {
              false
            }
          }
          case Some(bucket) => {
            if (!cos.doesObjectExist(bucket, s"${part}/")) {
              val por = new PutObjectRequest(bucket,
                s"${part}/", CosFileSystem.emptyContent, CosFileSystem.metadata);
              cos.putObject(por)
              //TODO check result here
              true
            } else {
              false
            }
          }
        }
      )
    }

    val folders = filePath.split(File.separatorChar).toList
    cosOpt.map(cos =>
      folders match {
        case Nil => /*do nothing */ true
        case head :: Nil =>
          createFolderIfDoesNotExist(head, None).getOrElse(false)
        case head :: tail => createFolderIfDoesNotExist(tail.mkString("/"), Some(head)).getOrElse(false)
      }).getOrElse(false)
  }

  /**
    * @param path
    * @return boolean
    */
  override def deletePath(path: String): Boolean = {
    val folders = path.split(File.separatorChar).toList
    cosOpt.map(cos =>
      folders match {
        case Nil => /*do nothing */ true
        case bucket :: Nil =>
          if (cos.doesBucketExist(bucket)){
            cos.deleteBucket(bucket)
          }
          true
        case bucket :: tail => {
          val key = tail.mkString("/")
          if (cos.doesObjectExist(bucket,key))
            cos.listObjects(bucket, key).getObjectSummaries().foreach(item => cos.deleteObject(bucket, item.getKey))
            cos.deleteObject(bucket, key)
            true
        }
      }).getOrElse(false)
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
    * @param remotePath
    * @return boolean
    */
  override def fileExists(remotePath: String): Boolean = {
    val splits = remotePath.split(File.separatorChar).toList
    cosOpt.map(cos =>
      splits match {
        case Nil => /*do nothing */ true
        case bucket :: Nil =>
          cos.doesBucketExist(bucket)
        case bucket :: tail => {
          val key = tail.mkString("/")
          cos.doesObjectExist(bucket,key)
        }
      }).getOrElse(false)
  }

}
