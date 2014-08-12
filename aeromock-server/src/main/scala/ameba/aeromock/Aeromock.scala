package ameba.aeromock

import ameba.aeromock.config.{ServerOption, ServerOptionDef, ServerOptionRepository}
import ameba.aeromock.core.bootstrap.BootstrapManager
import ameba.aeromock.server.AeromockServer
import org.kohsuke.args4j.CmdLineParser
import org.slf4j.LoggerFactory

/**
 * launcher of Aeromock
 * @author stormcat24
 */
object Aeromock extends App {

  println("Welcome to")
  println(AeromockInfo.splash)

  val option = new ServerOptionDef
  val parser = new CmdLineParser(option)
  parser.parseArgument(args: _*)

  ServerOptionRepository.option = ServerOption(option)

  val LOG = LoggerFactory.getLogger(this.getClass())

  LOG.info(s"configuration file = ${ServerOptionRepository.configFile.toAbsolutePath.toString}")
  LOG.info(s"listening port = ${ServerOptionRepository.listenPort}")

  try {
    BootstrapManager.delegate
    new AeromockServer(ServerOptionRepository.listenPort).run
    LOG.info("Aeromock Server Running.")
  } catch {
    case e: Exception => LOG.error("Failed to start Aeromock Server.", e)
  }

}
