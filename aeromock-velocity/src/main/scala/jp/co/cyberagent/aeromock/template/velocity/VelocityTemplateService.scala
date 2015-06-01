package jp.co.cyberagent.aeromock.template.velocity

import java.io.StringWriter

import jp.co.cyberagent.aeromock.core.annotation.TemplateIdentifier
import jp.co.cyberagent.aeromock.core.http.AeromockHttpRequest
import jp.co.cyberagent.aeromock.data.InstanceProjection
import jp.co.cyberagent.aeromock.template.TemplateService
import jp.co.cyberagent.aeromock.{AeromockTemplateNotFoundException, AeromockTemplateParseException}
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.Velocity
import org.apache.velocity.exception.{ParseErrorException, ResourceNotFoundException}
import scaldi.Injector

import scala.language.dynamics

/**
 * [[jp.co.cyberagent.aeromock.template.TemplateService]] for Velocity.
 * @author stormcat24
 */
@TemplateIdentifier(name = "velocity", configType = classOf[Void], specialConfig = true)
class VelocityTemplateService(implicit val inj: Injector) extends TemplateService {

  VelocityConfigurationInitializer.initialize(project)

  /**
   * @inheritdoc
   */
  override def renderHtml(request: AeromockHttpRequest, projection: InstanceProjection): String = {

    val templatePath = request.url + extension
    val template = try {
      Velocity.getTemplate(templatePath)
    } catch {
      case e: ResourceNotFoundException => throw new AeromockTemplateNotFoundException(request.url + extension, e)
      case e: ParseErrorException => throw new AeromockTemplateParseException(templatePath, e)
    }

    val proxyMap = projection.toInstanceJava().asInstanceOf[java.util.Map[_, _]]
    val context = new VelocityContext(proxyMap)

    val writer = new StringWriter
    template.merge(context, writer)
    writer.toString
  }

  /**
   * @inheritdoc
   */
  override def extension: String = ".vm"
}
