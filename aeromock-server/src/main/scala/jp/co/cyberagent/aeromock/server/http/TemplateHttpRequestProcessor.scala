package jp.co.cyberagent.aeromock.server.http

import jp.co.cyberagent.aeromock.config.ConfigHolder
import jp.co.cyberagent.aeromock.config.entity.Project
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.{FullHttpRequest, HttpResponse, HttpResponseStatus}
import jp.co.cyberagent.aeromock.AeromockBadUsingException

/**
 * [[jp.co.cyberagent.aeromock.server.http.HttpRequestProcessor]] for Template Request.
 * @author stormcat24
 */
object TemplateHttpRequestProcessor extends HttpRequestProcessor with HttpResponseWriter {

  override def process(project: Project, request: FullHttpRequest)
    (implicit context: ChannelHandlerContext): HttpResponse = {

    ConfigHolder.getTemplateService match {
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
