package jp.co.cyberagent.aeromock.template

import jp.co.cyberagent.aeromock.config.{Project, ServerOptionRepository, TemplateContext}

/**
 * Management object of [[TemplateContext]].
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
