package jp.co.cyberagent.aeromock.template.handlebars

import java.io.{FileNotFoundException, StringWriter}

import com.github.jknack.handlebars.HandlebarsException
import jp.co.cyberagent.aeromock.core.annotation.TemplateIdentifier
import jp.co.cyberagent.aeromock.core.http.{AeromockHttpRequest, VariableManager}
import jp.co.cyberagent.aeromock.data.InstanceProjection
import jp.co.cyberagent.aeromock.template._
import jp.co.cyberagent.aeromock.{AeromockTemplateNotFoundException, AeromockTemplateParseException}
import scaldi.Injector

import scala.language.dynamics
import scalaz.Scalaz._

/**
 * [[jp.co.cyberagent.aeromock.template.TemplateService]] for Handlebars.java
 * @author stormcat24
 */
@TemplateIdentifier(name = "handlebars", configType = classOf[HandlebarsConfigDef])
class HandlebarsTemplateService(config: HandlebarsConfig)(implicit val inj: Injector) extends TemplateService {

  val handlebars = HandlebarsFactory.create(project, config)

  /**
   * @inheritdoc
   */
  override def renderHtml(request: AeromockHttpRequest, projection: InstanceProjection): String = {
    val template = try {
      handlebars.compile(request.url)
    } catch {
      case e: FileNotFoundException => throw new AeromockTemplateNotFoundException(request.url + config.suffix, e)
      case e: HandlebarsException => throw new AeromockTemplateParseException(request.url + config.suffix, e)
    }

    val proxyMap = projection.toInstanceJava().asInstanceOf[java.util.Map[_, _]]
    VariableManager.initializeDataMap(proxyMap)

    val out = new StringWriter
    template.apply(proxyMap, out)
    out.toString()
  }

  def extension: String = config.suffix | ".hbs"
}
