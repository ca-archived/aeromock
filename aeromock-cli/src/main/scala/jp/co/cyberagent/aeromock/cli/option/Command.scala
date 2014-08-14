package jp.co.cyberagent.aeromock.cli.option

import org.kohsuke.args4j._
import org.kohsuke.args4j.spi.{MapOptionHandler, StringArrayOptionHandler}

/**
 * Command line options of Aeromock CLI
 * @author stormcat24
 */
class Command {

  @Option(name = "-v", usage = "show version")
  var version: Boolean = false

  @Option(name = "-h", usage = "show usage message")
  var help: Boolean = false

  @Option(name = "-d", usage = "display debug log")
  var debug: Boolean = false

  @Argument(index = 0, metaVar = "job")
  var job: String = null

  @Argument(index = 1, metaVar = "arguments...", handler = classOf[StringArrayOptionHandler])
  var arguments: Array[String] = null

  @Option(name = "-D", handler = classOf[MapOptionHandler])
  var jobOptions: java.util.Map[String, String] = null

  def toJobOperation(): JobOperation = {
    import scala.collection.JavaConversions._

    JobOperation(
      if (arguments == null || arguments.isEmpty) Seq.empty else arguments.toSeq,
      if (jobOptions == null || jobOptions.isEmpty) Map.empty else jobOptions.toMap
    )
  }

}

case class JobOperation(arguments: Seq[String], jobOptions: Map[String, String])
