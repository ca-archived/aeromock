package ameba.aeromock.cli

import ameba.aeromock.AeromockInfo
import ameba.aeromock.cli.job.CliJobs
import ameba.aeromock.cli.option.Command
import ameba.aeromock.helper._
import org.apache.commons.lang3.StringUtils
import org.kohsuke.args4j.CmdLineParser
import org.slf4j.LoggerFactory

/**
 * Command line interface of Aeromock.
 * @author stormcat24
 */
object AeromockCli {

  val log = LoggerFactory.getLogger(this.getClass())

  def main(args: Array[String]) {

    val command = new Command

    val parser = new CmdLineParser(command)
    parser.parseArgument(args: _*)

    if (command.help) {
      printUsage(parser)
      sys.exit(0)
    }

    if (command.version) {
      println(s"""Aeromock CLI ver ${lightBlue(AeromockInfo.version)}""")
      sys.exit(0)
    }

    if (StringUtils.isBlank(command.job)) {
      System.err.println(
        """not specified job name!
          |
        """.stripMargin)
      printUsage(parser)
      sys.exit(1)
    }

    try {
      log.info("Starting job...")
      CliJobSelector.select(command).execute
      log.info("Job Finished Successfully.")
      sys.exit(0)
    } catch {
      case e: Exception => {
        log.error(s"An unexpected error has occurred by '${command.job}' job.")
        if (command.debug) {
          log.error("caused by", e)
        }
        log.error("Unfortunately, Job Failed.")
        sys.exit(1)
      }
    }

  }

  private def printUsage(parser: CmdLineParser) {
    println("""
              |Usage:
              |  aeromock-cli [options] <jobname> [arguments...]
              |
              |Available Jobs:""".stripMargin)

    CliJobs.availableJobs.foreach(job => {
      println(s" ${job._1.name()}: ${job._1.description()}")
    })

    println("\nOptions:")
    parser.printUsage(System.out)
  }

}
