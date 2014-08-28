package jp.co.cyberagent.aeromock.template.freemarker.directive

import jp.co.cyberagent.aeromock.core.http.VariableManager
import jp.co.cyberagent.aeromock.core.script.GroovyDirectiveScriptRunner
import freemarker.core.Environment
import freemarker.template.{TemplateDirectiveBody, TemplateDirectiveModel, TemplateModel}
import groovy.lang.Binding

class AeromockScriptDirective(
  runner: GroovyDirectiveScriptRunner,
  directiveName: String) extends AeromockCustomDirective with TemplateDirectiveModel {

  override def execute(env: Environment, params: java.util.Map[_, _],
    loopVars: Array[TemplateModel], body: TemplateDirectiveBody) {

    val binding = new Binding
    binding.setVariable("_env", env)
    binding.setVariable("_params", params)
    binding.setVariable("_loopVars", loopVars)
    binding.setVariable("_body", body)
    binding.setVariable("_writer", env.getOut())

    VariableManager.getRequestMap().foreach(entry => binding.setVariable(entry._1, entry._2))
    binding.setVariable("_data", VariableManager.getDataMap())

    runner.run[Unit](getDirevtiveName() + ".groovy", binding)
  }

  override def getDirevtiveName(): String = directiveName
}
