package jp.co.cyberagent.aeromock.cli

import jp.co.cyberagent.aeromock.cli.job.CliJobs
import jp.co.cyberagent.aeromock.cli.option.CliCommand
import jp.co.cyberagent.aeromock.core.bootstrap.BootstrapManager
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.{AeromockBadUsingException, AeromockSystemException}
import scaldi.{Injectable, Injector}

/**
 * object to select executed [[jp.co.cyberagent.aeromock.cli.CliJob]].
 * @author stormcat24
 */
class CliJobSelector(implicit inj: Injector) extends AnyRef with Injectable {

  /**
   * Select correct [[jp.co.cyberagent.aeromock.cli.CliJob]] from [[jp.co.cyberagent.aeromock.cli.option.CliCommand]].
   * @param command [[jp.co.cyberagent.aeromock.cli.option.CliCommand]]
   */
  def select(command: CliCommand): CliJob = {
    require(command != null)

    BootstrapManager.delegate
    val formattedJob = command.job.get.toLowerCase()

    CliJobs.jobs.collectFirst {
      case job if job.getAnnotation(classOf[Job]) != null && job.getAnnotation(classOf[Job]).name == formattedJob => job
    } match {
      case Some(jobType) => {
        val instances = jobType.getConstructors.flatMap(c => {
          tryo(c.newInstance(command.jobOperation, inj).asInstanceOf[CliJob])
        })

        if (instances.isEmpty) {
          throw new AeromockSystemException(s"cannot create job instance.")
        }
        instances(0)
      }
      case None => throw new AeromockBadUsingException(s"job '${formattedJob}' is not defined.")
    }
  }

}
