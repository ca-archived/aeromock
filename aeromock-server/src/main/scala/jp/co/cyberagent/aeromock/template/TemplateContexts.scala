package jp.co.cyberagent.aeromock.template

import jp.co.cyberagent.aeromock.config.{Project, TemplateContext}
import scaldi.{Injectable, Injector}

/**
 * Management object of [[TemplateContext]].
 * @author stormcat24
 */
object TemplateContexts extends AnyRef with Injectable {

  def getAllContexts(project: Project)(implicit inj: Injector): List[TemplateContext] = {
    if (project._template.contexts.isEmpty) {
      List(TemplateContext("localhost", inject[Int](identified by 'listenPort), project._template.root))
    } else {
      project._template.contexts
    }
  }

}
