package ameba.aeromock.cli

import ameba.aeromock.cli.option.JobOperation
import ameba.aeromock.config.entity.Project
import ameba.aeromock.template.TemplateService

/**
 * Base job class.
 * @author stormcat24
 */
abstract class CliJob(command: JobOperation, project: Project, templateService: Option[TemplateService]) {

  /**
   * execute job.
   * @return return code
   */
  def execute(): Int

}
