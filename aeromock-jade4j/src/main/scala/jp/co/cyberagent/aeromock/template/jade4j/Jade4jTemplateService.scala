package jp.co.cyberagent.aeromock.template.jade4j

import java.io.{FileNotFoundException, StringWriter, Writer}

import de.neuland.jade4j.exceptions.JadeParserException
import jp.co.cyberagent.aeromock.config.Project
import jp.co.cyberagent.aeromock.core.annotation.TemplateIdentifier
import jp.co.cyberagent.aeromock.core.http.{AeromockHttpRequest, VariableManager}
import jp.co.cyberagent.aeromock.data.InstanceProjection
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.template.{TemplateAssertError, TemplateAssertFailure, TemplateAssertResult, TemplateService}
import jp.co.cyberagent.aeromock.{AeromockTemplateNotFoundException, AeromockTemplateParseException}
import scaldi.Injector

import scala.language.dynamics
import scalaz.Scalaz._

/**
 * [[jp.co.cyberagent.aeromock.template.TemplateService]] for Jade4J.
 * @author stormcat24
 */
@TemplateIdentifier(name = "jade4j", configType = classOf[Jade4jConfigDef])
class Jade4jTemplateService(config: Jade4jConfig)(implicit val inj: Injector) extends TemplateService {

  val configuration = JadeConfigurationFactory.create(project, config)

  /**
   * @inheritdoc
   */
  override def renderHtml(request: AeromockHttpRequest, projection: InstanceProjection): String = {
    val templatePath = if (request.url.startsWith("/")) {
      request.url.substring(1, request.url.length()) + extension
    } else {
      request.url + extension
    }

    val template = try {
      configuration.getTemplate(templatePath)
    } catch {
      case e: FileNotFoundException => throw new AeromockTemplateNotFoundException(templatePath, e)
      case e: JadeParserException => throw new AeromockTemplateParseException("Failed to parse Jade4j template.", e)
    }

    val proxyMap = projection.toInstanceJava().asInstanceOf[java.util.Map[_, _]]

    val out = new StringWriter
    VariableManager.initializeDataMap(proxyMap)
    configuration.renderTemplate(template, proxyMap.asInstanceOf[java.util.Map[String, AnyRef]], out)
    out.toString
  }

  /**
   * @inheritdoc
   */
  override def templateAssertProcess(templatePath: String): Either[TemplateAssertResult, (Any, Writer) => Unit] = {
    val startTimeMills = System.currentTimeMillis()
    try {
      Right((param: Any, writer: Writer) => {
        configuration.renderTemplate(configuration.getTemplate(templatePath), param.asInstanceOf[java.util.Map[String, AnyRef]], writer)
      })
    } catch {
      case e: JadeParserException => Left(TemplateAssertFailure(getDifferenceSecondsFromNow(startTimeMills), e.getMessage))
      case e: Exception => Left(TemplateAssertError(getDifferenceSecondsFromNow(startTimeMills), e.getMessage))
    }
  }

  /**
   * @inheritdoc
   */
  override def extension: String = config.extension | ".jade"

}
