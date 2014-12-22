package jp.co.cyberagent.aeromock.api.controller

import jp.co.cyberagent.aeromock.core.http.AeromockHttpRequest
import jp.co.cyberagent.aeromock.server.http.HttpResponseWriter
import io.netty.handler.codec.http.HttpResponse
import io.netty.handler.codec.http.HttpResponseStatus._
import io.netty.channel.ChannelHandlerContext
import jp.co.cyberagent.aeromock.{AeromockTemplateNotFoundException, AeromockConfigurationException, AeromockTemplateParseException, AeromockApiBadRequestException}
import org.slf4j.LoggerFactory
import scaldi.Injectable

trait AeromockApiController extends HttpResponseWriter with Injectable {

  val LOG = LoggerFactory.getLogger(classOf[AeromockApiController])

  def render(request: AeromockHttpRequest)(implicit context: ChannelHandlerContext): HttpResponse = {

    try {
      renderJson(Map("status" -> OK.code()) ++ renderJson(request), OK)
    } catch {
      case e: AeromockApiBadRequestException => renderJson(Map("status" -> BAD_REQUEST.code(), "message" -> e.getMessage()), BAD_REQUEST)
      case e: AeromockTemplateParseException => renderJson(Map("status" -> BAD_REQUEST.code(), "message" -> e.getMessage()), BAD_REQUEST)
      case e: AeromockTemplateNotFoundException => renderJson(Map("status" -> BAD_REQUEST.code(), "message" -> e.getMessage()), BAD_REQUEST)
      case e: AeromockConfigurationException => renderJson(Map("status" -> FORBIDDEN.code(), "message" -> e.getMessage()), FORBIDDEN)
      case e: Exception => {
        e.printStackTrace()
        LOG.error(e.getMessage(), e.getCause())
        renderJson(
          Map(
            "status" -> INTERNAL_SERVER_ERROR.code(),
            "message" -> e.getMessage()), INTERNAL_SERVER_ERROR)
      }
    }
  }

  def renderJson(request: AeromockHttpRequest): Map[String, Any]

}
