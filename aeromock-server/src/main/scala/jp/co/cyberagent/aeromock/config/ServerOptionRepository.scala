package jp.co.cyberagent.aeromock.config

import java.nio.file.Paths

import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.AeromockInfo

import scalaz.Scalaz._

object ServerOptionRepository {

  var option: ServerOption = null

  def configFile = Option(option).flatMap(_.configFile) | Paths.get("~/.aeromock/config.yaml").withHomeDirectory

  def listenPort = Option(option).flatMap(_.port) | AeromockInfo.defaultListenPort

}
