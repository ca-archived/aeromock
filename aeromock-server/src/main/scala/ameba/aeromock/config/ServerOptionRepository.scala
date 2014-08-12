package ameba.aeromock.config

import java.nio.file.Paths

import ameba.aeromock.helper._
import ameba.aeromock.AeromockInfo

import scalaz.Scalaz._

object ServerOptionRepository {

  var option: ServerOption = null

  def configFile = Option(option).flatMap(_.configFile) | Paths.get("~/.aeromock/config.yaml").withHomeDirectory

  def listenPort = Option(option).flatMap(_.port) | AeromockInfo.defaultListenPort

}
