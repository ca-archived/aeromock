package jp.co.cyberagent.aeromock.cli

import jp.co.cyberagent.aeromock.cli.job.CliJobs
import jp.co.cyberagent.aeromock.cli.option.CliCommand
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.{AeromockAppModule, AeromockInfo}
import org.slf4j.LoggerFactory
import scaldi.Injectable

/**
 * Command line interface of Aeromock.
 * @author stormcat24
 */
object AeromockCli extends AnyRef with Injectable {

  val log = LoggerFactory.getLogger(this.getClass())

  implicit val printUsageBefore = () => {
    println("""
              |Usage:
              |  aeromock-cli [options] <jobname> [arguments...]
              |
              |Available Jobs:""".stripMargin)

    CliJobs.availableJobs.foreach(job => {
      println(s" ${job._1.name()}: ${job._1.description()}")
    })
  }

  def main(args: Array[String]) {

    val command = CliCommand(args)

    if (command.help) {
      command.printUsage
      sys.exit(0)
    }

    if (command.version) {
      println(s"""Aeromock CLI ver ${lightBlue(AeromockInfo.version)}""")
      sys.exit(0)
    }

    command.job match {
      case None => {
        System.err.println(
          """not specified job name!
            |
          """.stripMargin)
        command.printUsage
        sys.exit(1)
      }
      case _ =>
    }

    try {
      implicit val module = new AeromockAppModule(command.configFile match {
        case Some(configFile) => Array(s"-c $configFile")
        case None => Array.empty[String]
      }) ++ new AeromockCliModule

      log.info("Starting job...")
      inject[CliJobSelector].select(command).execute
      log.info("Job Finished Successfully.")
      sys.exit(0)
    } catch {
      case e: Exception => {
        log.error(s"An unexpected error has occurred by '${command.job}' job.", e)
        if (command.debug) {
          log.error("caused by", e)
        }
        log.error("Unfortunately, Job Failed.")
        sys.exit(1)
      }
    }

  }

}
