package ameba.aeromock.cli

import ameba.aeromock.cli.option.Command
import ameba.aeromock.cli.job.CliJobs
import ameba.aeromock.config.ConfigHolder
import ameba.aeromock.core.bootstrap.BootstrapManager
import ameba.aeromock.helper._
import ameba.aeromock.{AeromockBadUsingException, AeromockSystemException}

/**
 * object to select executed [[ameba.aeromock.cli.CliJob]].
 * @author stormcat24
 */
object CliJobSelector {

  /**
   * Select correct [[ameba.aeromock.cli.CliJob]] from [[ameba.aeromock.cli.option.Command]].
   * @param command [[ameba.aeromock.cli.option.Command]]
   */
  def select(command: Command): CliJob = {
    require(command != null)

    BootstrapManager.delegate
    ConfigHolder.initialize()

    val formattedJob = command.job.toLowerCase()

    CliJobs.jobs.collectFirst {
      case job if job.getAnnotation(classOf[Job]) != null && job.getAnnotation(classOf[Job]).name == formattedJob => job
    } match {
      case Some(jobType) => {
        val instances = jobType.getConstructors.flatMap(c => {
          tryo(c.newInstance(command.toJobOperation, ConfigHolder.getProject, ConfigHolder.getTemplateService).asInstanceOf[CliJob])
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
