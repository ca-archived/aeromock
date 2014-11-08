package jp.co.cyberagent.aeromock.cli

import java.io.Writer

import jp.co.cyberagent.aeromock.core.annotation.TemplateIdentifier
import jp.co.cyberagent.aeromock.core.http.ParsedRequest
import jp.co.cyberagent.aeromock.data.InstanceProjection
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.template.{TemplateAssertFailure, TemplateAssertResult, TemplateService}
import scaldi.Injector


/**
 *
 * @author stormcat24
 */
@TemplateIdentifier(name = "test", configType = classOf[Void], specialConfig = true)
class TestTemplateService(implicit val inj: Injector) extends TemplateService {
  override protected def renderHtml(request: ParsedRequest, projection: InstanceProjection): String = "test"

  override def templateAssertProcess(templatePath: String): Either[TemplateAssertResult, (Any, Writer) => Unit] = {
    val startTimeMills = System.currentTimeMillis()
    try {
      Right((param: Any, writer: Writer) => println())
    } catch {
      case e: Exception => Left(TemplateAssertFailure(getDifferenceSecondsFromNow(startTimeMills), e.getMessage))
    }
  }

  /**
   * @return extension
   */
  override def extension: String = ".tmpl"
}
