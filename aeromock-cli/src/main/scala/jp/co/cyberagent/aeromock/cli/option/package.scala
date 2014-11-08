package jp.co.cyberagent.aeromock.cli

import org.apache.commons.lang3.StringUtils
import org.kohsuke.args4j.{CmdLineParser, Argument}
import org.kohsuke.args4j.spi.{MapOptionHandler, StringArrayOptionHandler}

import scala.collection.JavaConverters._

/**
 *
 * @author stormcat24
 */
package object option {

  type Args4jOption = org.kohsuke.args4j.Option

  /**
   * Command line options of Aeromock CLI
   * @author stormcat24
   */
  class CliCommandDef {

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
  }

  class CliCommand(
    val parser: CmdLineParser,
    val version: Boolean,
    val help: Boolean,
    val debug: Boolean,
    val configFile: Option[String],
    val job: Option[String],
    val jobOperation: JobOperation
  ) {

    def printUsage(implicit before : (Unit => Unit)): Unit = {
      before()
      println("\nOptions:")
      parser.printUsage(System.out)
    }
  }

  object CliCommand {
    def apply(args: Array[String]): CliCommand = {
      val commandDef = new CliCommandDef
      val parser = new CmdLineParser(commandDef)
      parser.parseArgument(args: _*)


      new CliCommand(
        parser,
        commandDef.version,
        commandDef.help,
        commandDef.debug,
        Option(commandDef.configFile).flatMap(s => if (StringUtils.isBlank(s)) None else Some(s)),
        Option(commandDef.job).flatMap(s => if (StringUtils.isBlank(s)) None else Some(s)),
        JobOperation(
          if (commandDef.arguments == null || commandDef.arguments.isEmpty) Seq.empty else commandDef.arguments.toSeq,
          if (commandDef.jobOptions == null || commandDef.jobOptions.isEmpty) Map.empty else commandDef.jobOptions.asScala.toMap
        )
      )
    }

  }

  case class JobOperation(arguments: Seq[String], jobOptions: Map[String, String])
}
