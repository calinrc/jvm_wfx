package org.cgc.wfx

import spray.json._
import spray.json.DefaultJsonProtocol

case class WfxPairsConfigurations(configurations: Seq[PairConfig])

case class PairConfig(className: String, properties: Option[Map[String, String]])


object WfxPairConfigurationJsonFormat extends DefaultJsonProtocol {

  implicit val pairConfigurationFormat = jsonFormat2(PairConfig.apply)
  implicit val pairConfigurationsFormat = jsonFormat1(WfxPairsConfigurations.apply)

}