package jp.co.cyberagent.aeromock.template.freemarker

import java.io.{FileNotFoundException, StringWriter, Writer}

import _root_.freemarker.core.ParseException
import jp.co.cyberagent.aeromock.core.annotation.TemplateIdentifier
import jp.co.cyberagent.aeromock.core.http.{AeromockHttpRequest, VariableManager}
import jp.co.cyberagent.aeromock.data.InstanceProjection
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.template._
import jp.co.cyberagent.aeromock.{AeromockTemplateNotFoundException, AeromockTemplateParseException}
import scaldi.Injector

import scala.language.dynamics

/**
 * [[jp.co.cyberagent.aeromock.template.TemplateService]] for Freemarker.
 * @author stormcat24
 */
@TemplateIdentifier(name = "freemarker", configType = classOf[FreemarkerConfigDef])
class FreemarkerTemplateService(config: FreemarkerConfig)(implicit val inj: Injector) extends TemplateService {

  val configuration = ConfigurationFactory.create(
    project._template.root, project.templateScript, config, project.tag, project.function)

  /**
   * @inheritdoc
   */
  override def renderHtml(request: AeromockHttpRequest, projection: InstanceProjection): String = {
    val templatePath = request.url + config.extension
    val template = try {
      configuration.getTemplate(templatePath)
    } catch {
      case e: FileNotFoundException => throw new AeromockTemplateNotFoundException(templatePath, e)
      case e: ParseException => throw new AeromockTemplateParseException(templatePath, e)
    }

    val proxyMap = projection.toInstanceJava().asInstanceOf[java.util.Map[_, _]]
    VariableManager.initializeDataMap(proxyMap)

    val out = new StringWriter
    template.process(proxyMap, out)
    out.toString()
  }

  /**
   * @inheritdoc
   */
  override def templateAssertProcess(templatePath: String): Either[TemplateAssertResult, (Any, Writer) => Unit] = {
    val startTimeMills = System.currentTimeMillis()
    try {
      Right((param: Any, writer: Writer) => configuration.getTemplate(templatePath).process(param, writer))
    } catch {
      case e: ParseException => Left(TemplateAssertFailure(getDifferenceSecondsFromNow(startTimeMills), e.getMessage))
      case e: Exception => Left(TemplateAssertError(getDifferenceSecondsFromNow(startTimeMills), e.getMessage))
    }
  }

  /**
   * @inheritdoc
   */
  override def extension: String = config.extension

}

