package ameba.aeromock.template.handlebars

import ameba.aeromock.{AeromockScriptExecutionException, AeromockConfigurationException}
import ameba.aeromock.config.entity._
import ameba.aeromock.core.script.GroovyScriptRunner
import ameba.aeromock.template.handlebars.helper.DynamicHelperMethod
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.helper.StringHelpers
import com.github.jknack.handlebars.io.FileTemplateLoader
import groovy.lang.Binding

import scalaz.Scalaz._
import scalaz._
import ameba.aeromock.helper._

/**
 * Factory to create [[com.github.jknack.handlebars.Handlebars]].
 * @author stormcat24
 */
object HandlebarsFactory {

  def create(project: Project,
    config: HandlebarsConfig): Handlebars = {

    val loader = new FileTemplateLoader(project._template.root.toFile)
    // forbid to set prefix
    config.suffix.map(v => loader.setSuffix(v))

    val handlebars = new Handlebars(loader)
    config.prettyPrint.map(v => handlebars.setPrettyPrint(v))
    config.stringParams.map(v => handlebars.setStringParams(v))
    config.infiniteLoops.map(v => handlebars.setInfiniteLoops(v))
    config.deletePartialAfterMerge.map(v => handlebars.setDeletePartialAfterMerge(v))
    config.startDelimiter.map(v => handlebars.setStartDelimiter(v))
    config.endDelimiter.map(v => handlebars.setEndDelimiter(v))

    val functionRootDir = project.function match {
      case Success(Some(value)) => value.root.some
      case Failure(errors) => throw new AeromockConfigurationException(project.projectConfig, errors)
      case _ => None
    }

    functionRootDir match {
      case None =>
      case Some(functionRoot) => {
        functionRoot.getChildren().foreach { script =>
          val scriptName = script.getFileName().toString()
          handlebars.registerHelper(scriptName.replace(".groovy", ""), new DynamicHelperMethod(script))
        }
      }
    }

    // hook script
    if (project.templateScript.exists()) {
      val binding = new Binding()

      binding.setProperty("configuration", config)
      new GroovyScriptRunner[Unit](project.templateScript).run(binding)
    }

    // add StringHelpers
    StringHelpers.values().toList.foreach { helper =>
      handlebars.registerHelper(helper.name(), helper)
    }

    handlebars
  }
}
