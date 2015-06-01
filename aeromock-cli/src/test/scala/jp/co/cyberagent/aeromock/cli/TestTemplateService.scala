package jp.co.cyberagent.aeromock.cli

import jp.co.cyberagent.aeromock.core.annotation.TemplateIdentifier
import jp.co.cyberagent.aeromock.core.http.AeromockHttpRequest
import jp.co.cyberagent.aeromock.data.InstanceProjection
import jp.co.cyberagent.aeromock.template.TemplateService
import scaldi.Injector


/**
 *
 * @author stormcat24
 */
@TemplateIdentifier(name = "test", configType = classOf[Void], specialConfig = true)
class TestTemplateService(implicit val inj: Injector) extends TemplateService {
  override protected def renderHtml(request: AeromockHttpRequest, projection: InstanceProjection): String = "test"

  /**
   * @return extension
   */
  override def extension: String = ".tmpl"
}
