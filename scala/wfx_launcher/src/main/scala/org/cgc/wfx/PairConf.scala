package org.cgc.wfx

case class PairConfigurations(configurations:Seq[PairConfiguration])

case class PairConfiguration(endpointUrl:String, username:String, password:String)
