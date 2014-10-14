package jp.co.cyberagent.aeromock.server.http

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.{FullHttpRequest, HttpResponse, HttpResponseStatus}
import jp.co.cyberagent.aeromock.AeromockBadUsingException
import jp.co.cyberagent.aeromock.config.Project
import jp.co.cyberagent.aeromock.template.TemplateService
import scaldi.Injector

/**
 * [[jp.co.cyberagent.aeromock.server.http.HttpRequestProcessor]] for Template Request.
 * @author stormcat24
 */
class TemplateHttpRequestProcessor(implicit inj: Injector) extends HttpRequestProcessor with HttpResponseWriter {

  val project = inject[Project]

  override def process(request: FullHttpRequest)(implicit context: ChannelHandlerContext): HttpResponse = {

    val templateService = inject[Option[TemplateService]]

    templateService match {
      case None => throw new AeromockBadUsingException("cannot get TemplateService.")
      case Some(templateService) => {
        templateService.render(request) match {
          case RenderResult(content, _, true) => renderYaml(content, HttpResponseStatus.OK)
          case RenderResult(content, customResponse, false) => renderHtml(content, HttpResponseStatus.OK, customResponse)
        }
      }
    }

  }

}
