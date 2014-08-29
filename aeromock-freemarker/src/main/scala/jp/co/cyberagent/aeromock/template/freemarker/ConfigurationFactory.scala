package jp.co.cyberagent.aeromock.template.freemarker

import java.nio.file.Path

import jp.co.cyberagent.aeromock.config.{Function, Tag}
import jp.co.cyberagent.aeromock.core.script.{GroovyDirectiveScriptRunner, GroovyScriptRunner}
import jp.co.cyberagent.aeromock.helper._
import freemarker.cache.FileTemplateLoader
import freemarker.template.Configuration
import groovy.lang.Binding
import jp.co.cyberagent.aeromock.template.freemarker.directive.DynamicDirectiveHelper
import jp.co.cyberagent.aeromock.template.freemarker.method.AeromockCustomMethod

import scalaz._
import Scalaz._
import scala.collection.JavaConverters._

/**
 * Factory object of [[freemarker.template.Configuration]].
 * @author stormcat24
 */
object ConfigurationFactory {

  def create(templateRoot: Path,
    templateScript: Path,
    config: FreemarkerConfig,
    tag: ValidationNel[String, Option[Tag]],
    function: ValidationNel[String, Option[Function]]): Configuration = {

    val configuration = new Configuration
    configuration.setObjectWrapper(config.objectWrapper)

    val tagRootDir = tag match {
      case Success(Some(value)) => value.root.some
      case _ => None
    }
    val functionRootDir = function match {
      case Success(Some(value)) => value.root.some
      case _ => None
    }

    // for custom directive and function self
    DynamicDirectiveHelper.getDynamicDirectives(templateScript, tagRootDir, config).foreach(directive => {
      configuration.setSharedVariable(directive.getDirevtiveName(), directive)
    })

    functionRootDir match {
      case None =>
      case Some(functionRoot) => {
        val runner = new GroovyDirectiveScriptRunner(functionRoot)
        functionRoot.getChildren().foreach { script =>
          val scriptName = script.getFileName().toString()
          configuration.setSharedVariable(scriptName.replace(".groovy", ""), new AeromockCustomMethod(runner, scriptName))
        }
      }
    }

    config.autoEscape.map {
      case true => new AutoEscapeTemplateLoader(templateRoot)
      case false => new FileTemplateLoader(templateRoot.toFile())
    }.map(configuration.setTemplateLoader(_))
    config.autoFlush.map(v => configuration.setAutoFlush(v))
    configuration.setAutoIncludes(config.autoIncludes.asJava)
    config.booleanFormat.map(v => configuration.setBooleanFormat(v))
    config.classicCompatible.map(v => configuration.setClassicCompatible(v))
    config.classicCompatibleAsInt.map(v => configuration.setClassicCompatibleAsInt(v))
    config.dateFormat.map(v => configuration.setDateFormat(v))
    config.dateTimeFormat.map(v => configuration.setDateTimeFormat(v))
    config.defaultEncoding.map(v => configuration.setDefaultEncoding(v))
    config.localizedLookup.map(v => configuration.setLocalizedLookup(v))
    config.numberFormat.map(v => configuration.setNumberFormat(v))
    config.outputEncoding.map(v => configuration.setOutputEncoding(v))
    config.strictBeanModels.map(v => configuration.setStrictBeanModels(v))
    config.strictSyntaxMode.map(v => configuration.setStrictSyntaxMode(v))
    config.tagSyntax.map(v => configuration.setTagSyntax(v))
    config.urlEscapingCharset.map(v => configuration.setURLEscapingCharset(v))
    config.whitespaceStripping.map(v => configuration.setWhitespaceStripping(v))
    configuration.setAutoImports(config.autoImports.asJava)
    config.arithmeticEngine.map(v => configuration.setArithmeticEngine(v))

    // hook script
    if (templateScript.exists()) {
      val binding = new Binding()
      binding.setProperty("configuration", configuration)
      new GroovyScriptRunner[Unit](templateScript).run(binding)
    }

    configuration
  }

}
