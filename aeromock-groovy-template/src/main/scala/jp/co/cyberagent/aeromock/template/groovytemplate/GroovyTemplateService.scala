package jp.co.cyberagent.aeromock.template.groovytemplate

import java.io.{IOException, Writer}
import java.nio.file.Paths

import jp.co.cyberagent.aeromock.config.Project
import jp.co.cyberagent.aeromock.core.annotation.TemplateIdentifier
import jp.co.cyberagent.aeromock.core.http.ParsedRequest
import jp.co.cyberagent.aeromock.data.InstanceProjection
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.template.{TemplateAssertError, TemplateAssertFailure, TemplateAssertResult, TemplateService}
import jp.co.cyberagent.aeromock.{AeromockTemplateNotFoundException, AeromockTemplateParseException}
import org.codehaus.groovy.control.CompilationFailedException
import scaldi.Injector

import scala.language.dynamics
import scalaz.Scalaz._

/**
 * [[jp.co.cyberagent.aeromock.template.TemplateService]] for GroovyTemplates
 * @author stormcat24
 */
@TemplateIdentifier(name = "groovyTemplate", configType = classOf[GroovyTemplateConfigDef])
class GroovyTemplateService(config: GroovyTemplateConfig)(implicit val inj: Injector) extends TemplateService {

  val engine = TemplateEngineFactory.create(config.mode)

  /**
   * @inheritdoc
   */
  override def renderHtml(request: ParsedRequest, projection: InstanceProjection): String = {
    require(request != null)

    val templatePath = project._template.root / request.url + extension
    val template = try {
      engine.createTemplate(templatePath.toFile)
    } catch {
      case e: IOException => throw new AeromockTemplateNotFoundException(templatePath.toString, e)
      case e: CompilationFailedException => throw new AeromockTemplateParseException(templatePath.toString, e)
    }

    val proxyMap = projection.toInstanceJava().asInstanceOf[java.util.Map[_, _]]
    template.make(proxyMap).toString
  }

  /**
   * @inheritdoc
   */
  override def templateAssertProcess(templatePath: String): Either[TemplateAssertResult, (Any, Writer) => Unit] = {
    val startTimeMills = System.currentTimeMillis()
    try {
      Right((param: Any, writer: Writer) => {
        engine.createTemplate(Paths.get(templatePath).toFile).make(param.asInstanceOf[java.util.Map[_, _]])
        println()
      })
    } catch {
      case e: IOException => Left(TemplateAssertError(getDifferenceSecondsFromNow(startTimeMills), e.getMessage))
      case e: CompilationFailedException => Left(TemplateAssertFailure(getDifferenceSecondsFromNow(startTimeMills), e.getMessage))
    }
  }

  /**
   * @inheritdoc
   */
  override def extension: String = config.extension | ".groovy"
}
