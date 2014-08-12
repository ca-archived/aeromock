package ameba.aeromock.template.handlebars.helper

import java.nio.file.Path

import ameba.aeromock.core.http.RequestManager
import ameba.aeromock.core.script.GroovyScriptRunner
import com.github.jknack.handlebars.{Helper, Options}
import groovy.lang.Binding

/**
 * Dynamic custom helper class for Handlebars.
 * @author stormcat24
 */
class DynamicHelperMethod(script: Path) extends Helper[AnyRef] {

  val runner = new GroovyScriptRunner[CharSequence](script)

  /**
   * @inheritdoc
   */
  override def apply(argument: AnyRef, options: Options): CharSequence = {

    val binding = new Binding
    binding.setVariable("argument", argument)
    RequestManager.getRequestMap().foreach(entry => binding.setVariable(entry._1, entry._2))
    binding.setVariable("_data", RequestManager.getDataMap())
    binding.setVariable("options", options)

    runner.run(binding)
  }
}
