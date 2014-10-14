package jp.co.cyberagent.aeromock.cli

import jp.co.cyberagent.aeromock.cli.job.CliJobs
import jp.co.cyberagent.aeromock.cli.option.Command
import jp.co.cyberagent.aeromock.config.Project
import jp.co.cyberagent.aeromock.core.bootstrap.BootstrapManager
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.template.TemplateService
import jp.co.cyberagent.aeromock.{AeromockAppModule, AeromockModule, AeromockBadUsingException, AeromockSystemException}
import scaldi.Injectable

/**
 * object to select executed [[jp.co.cyberagent.aeromock.cli.CliJob]].
 * @author stormcat24
 */
object CliJobSelector extends AnyRef with Injectable {

  /**
   * Select correct [[jp.co.cyberagent.aeromock.cli.CliJob]] from [[jp.co.cyberagent.aeromock.cli.option.Command]].
   * @param command [[jp.co.cyberagent.aeromock.cli.option.Command]]
   */
  def select(command: Command): CliJob = {
    require(command != null)

    BootstrapManager.delegate

    // TODO args
    implicit val injector = new AeromockAppModule(Array.empty)

    val formattedJob = command.job.toLowerCase()

    CliJobs.jobs.collectFirst {
      case job if job.getAnnotation(classOf[Job]) != null && job.getAnnotation(classOf[Job]).name == formattedJob => job
    } match {
      case Some(jobType) => {
        val project = inject[Project]
        val templateService = inject[Option[TemplateService]]
        val instances = jobType.getConstructors.flatMap(c => {
          tryo(c.newInstance(command.toJobOperation, project, templateService).asInstanceOf[CliJob])
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
