package cos_jvm_wfx.test

package cos_jvm_wfx_test

import org.cgc.wfx.impl.{CosConstants, CosFileSystem}
import org.cgc.wfx.{PairConfig, WfxPairsConfigurations}
import spray.json._
import org.cgc.wfx.WfxPairConfigurationJsonFormat._

import scala.reflect.io.{Directory, File}

object ConfigApp extends App {
  println("Start")

  val confVal = WfxPairsConfigurations(Seq(PairConfig(Some(CosFileSystem.getClass.getCanonicalName),
    Some(Map(CosConstants.ENDPOINT_URL -> "http://cosurl.cosss",
      CosConstants.ACCESS_KEY -> "myusername",
      CosConstants.SECRET_KEY -> "mypassword"
    )))))

  File(s"./config.json").writeAll(confVal.toJson.prettyPrint)
  println("End")

}
