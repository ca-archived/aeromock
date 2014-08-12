package ameba.aeromock.template.thymeleaf

import java.nio.file.Path
import java.util

import ameba.aeromock.helper._
import ameba.aeromock.core.script.GroovyScriptRunner
import groovy.lang.Binding
import org.thymeleaf.context.IProcessingContext
import org.thymeleaf.dialect.{AbstractDialect, IExpressionEnhancingDialect}

import scala.collection.JavaConverters._

/**
 * Dialect to create utility object dynamically.
 * @author stormcat24
 */
class DynamicUtilityObject(scriptDir: Path) extends AbstractDialect with IExpressionEnhancingDialect {

  val helpers = scriptDir.getChildren().filter(_.toString().endsWith(".groovy")).map(script => {
    val runner = new GroovyScriptRunner[AnyRef](script)
    val binding = new Binding()
    (script.getFileName.toString.replace(".groovy", ""), runner.run(binding))
  }).toMap

  override def getPrefix: String = "helper"

  override def getAdditionalExpressionObjects(processingContext: IProcessingContext): util.Map[String, AnyRef] = helpers.asJava
}
