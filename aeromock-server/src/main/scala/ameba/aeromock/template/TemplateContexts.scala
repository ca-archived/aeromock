package ameba.aeromock.template

import ameba.aeromock.config.ServerOptionRepository
import ameba.aeromock.config.entity.{Project, TemplateContext}

/**
 * Management object of [[ameba.aeromock.config.entity.TemplateContext]].
 * @author stormcat24
 */
object TemplateContexts {

  def getAllContexts(project: Project): List[TemplateContext] = {
    if (project._template.contexts.isEmpty) {
      List(TemplateContext("localhost", ServerOptionRepository.listenPort, project._template.root))
    } else {
      project._template.contexts
    }
  }

}
