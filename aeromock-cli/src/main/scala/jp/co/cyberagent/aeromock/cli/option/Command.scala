package jp.co.cyberagent.aeromock.cli.option

import org.kohsuke.args4j._
import org.kohsuke.args4j.spi.{MapOptionHandler, StringArrayOptionHandler}

/**
 * Command line options of Aeromock CLI
 * @author stormcat24
 */
class Command {

  type Args4jOption = org.kohsuke.args4j.Option

  @Args4jOption(name = "-v", usage = "show version")
  var version: Boolean = false

  @Args4jOption(name = "-h", usage = "show usage message")
  var help: Boolean = false

  @Args4jOption(name = "-d", usage = "display debug log")
  var debug: Boolean = false

  @Args4jOption(name = "-c", usage = "path of config file")
  var configFile: String = null

  @Argument(index = 0, metaVar = "job")
  var job: String = null

  @Argument(index = 1, metaVar = "arguments...", handler = classOf[StringArrayOptionHandler])
  var arguments: Array[String] = null

  @Args4jOption(name = "-D", handler = classOf[MapOptionHandler])
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
