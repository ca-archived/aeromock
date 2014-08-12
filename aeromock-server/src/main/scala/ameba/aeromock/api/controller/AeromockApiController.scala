package ameba.aeromock.api.controller

import ameba.aeromock.core.http.ParsedRequest
import ameba.aeromock.server.http.HttpResponseWriter
import ameba.aeromock.AeromockApiBadRequestException
import io.netty.handler.codec.http.HttpResponse
import io.netty.handler.codec.http.HttpResponseStatus._
import io.netty.channel.ChannelHandlerContext
import ameba.aeromock.AeromockConfigurationException
import org.slf4j.LoggerFactory
import ameba.aeromock.AeromockTemplateParseException
import ameba.aeromock.AeromockTemplateNotFoundException

trait AeromockApiController extends HttpResponseWriter {

  val LOG = LoggerFactory.getLogger(classOf[AeromockApiController])

  def render(request: ParsedRequest)(implicit context: ChannelHandlerContext): HttpResponse = {

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

  def renderJson(request: ParsedRequest): Map[String, Any]
}