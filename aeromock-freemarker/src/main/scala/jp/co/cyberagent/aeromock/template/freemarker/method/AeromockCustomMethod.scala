package jp.co.cyberagent.aeromock.template.freemarker.method

import jp.co.cyberagent.aeromock.core.http.VariableManager
import jp.co.cyberagent.aeromock.core.script.GroovyDirectiveScriptRunner
import freemarker.template.TemplateMethodModelEx
import groovy.lang.Binding

/**
 * Implementation of [[freemarker.template.TemplateMethodModelEx]] to define function in Groovy.
 * @author stormcat24
 */
class AeromockCustomMethod(runner: GroovyDirectiveScriptRunner, scriptName: String)
  extends TemplateMethodModelEx {

  /**
   * @inheritdoc
   */
  override def exec(arguments: java.util.List[_]): AnyRef = {
    val binding = new Binding
    binding.setVariable("arguments", arguments)
    VariableManager.getRequestMap().foreach(entry => binding.setVariable(entry._1, entry._2))
    binding.setVariable("_data", VariableManager.getDataMap())

    runner.run[AnyRef](scriptName, binding)
  }
}
