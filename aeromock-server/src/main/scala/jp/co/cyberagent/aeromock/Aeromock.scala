package jp.co.cyberagent.aeromock

import java.nio.file.Path

import jp.co.cyberagent.aeromock.core.bootstrap.BootstrapManager
import jp.co.cyberagent.aeromock.server.AeromockServer
import org.slf4j.LoggerFactory
import scaldi.Injectable

/**
 * launcher of Aeromock
 * @author stormcat24
 */
object Aeromock extends App with Injectable {

  println("Welcome to")
  println(AeromockInfo.splash)

  implicit val module = new AeromockAppModule(args)

  val LOG = LoggerFactory.getLogger(this.getClass())

  val configFile = inject[Path] (identified by 'configFile)
  val listenPort = inject[Int] (identified by 'listenPort)

  LOG.info(s"configuration file = ${configFile.toAbsolutePath.toString}")
  LOG.info(s"listening port = ${listenPort}")

  try {
    BootstrapManager.delegate
    new AeromockServer(listenPort).run
    LOG.info("Aeromock Server Running.")
  } catch {
    case e: Exception => LOG.error("Failed to start Aeromock Server.", e)
  }

}
