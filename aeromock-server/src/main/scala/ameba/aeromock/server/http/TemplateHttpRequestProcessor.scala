package ameba.aeromock.server.http

import ameba.aeromock.AeromockBadUsingException
import ameba.aeromock.config.ConfigHolder
import ameba.aeromock.config.entity.Project
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.{FullHttpRequest, HttpResponse, HttpResponseStatus}

/**
 * [[ameba.aeromock.server.http.HttpRequestProcessor]] for Template Request.
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
