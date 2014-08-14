package jp.co.cyberagent.aeromock.config

import java.nio.file.{Path, Paths}

import jp.co.cyberagent.aeromock.helper._

import scalaz.Scalaz._

class ServerOptionDef {

  type Args4jOption = org.kohsuke.args4j.Option

  @Args4jOption(name = "-p", usage = "listen port")
  var port: String = null

  @Args4jOption(name = "-c", usage = "path of config file")
  var configFile: String = null

}

class ServerOption(val port: Option[Int], val configFile: Option[Path])

object ServerOption {

  def apply(bean: ServerOptionDef): ServerOption = {
    new ServerOption(
      Option(bean.port).flatMap(_.parseInt.toOption),
      Option(bean.configFile).map(p => Paths.get(p).withHomeDirectory())
    )
  }
}
