package jp.co.cyberagent.aeromock.cli

import jp.co.cyberagent.aeromock.cli.option.JobOperation
import jp.co.cyberagent.aeromock.config.Project
import jp.co.cyberagent.aeromock.template.TemplateService
import org.slf4j.LoggerFactory
import scaldi.Injectable

/**
 * Base job trait.
 * @author stormcat24
 */
trait CliJob extends AnyRef with Injectable {

  val command: JobOperation

  val log = LoggerFactory.getLogger(this.getClass())

  /**
   * execute job.
   * @return return code
   */
  def execute(): Int

}
