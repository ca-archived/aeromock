package jp.co.cyberagent.aeromock.template.groovytemplate

import java.io.IOException

import jp.co.cyberagent.aeromock.core.annotation.TemplateIdentifier
import jp.co.cyberagent.aeromock.core.http.AeromockHttpRequest
import jp.co.cyberagent.aeromock.data.InstanceProjection
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.template.TemplateService
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
  override def renderHtml(request: AeromockHttpRequest, projection: InstanceProjection): String = {
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
  override def extension: String = config.extension | ".groovy"
}
