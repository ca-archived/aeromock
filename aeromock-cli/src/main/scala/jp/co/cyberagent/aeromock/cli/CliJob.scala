package jp.co.cyberagent.aeromock.cli

import jp.co.cyberagent.aeromock.cli.option.JobOperation
import jp.co.cyberagent.aeromock.config.entity.Project
import jp.co.cyberagent.aeromock.template.TemplateService

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
