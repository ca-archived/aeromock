package jp.co.cyberagent.aeromock.server.http

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.HttpResponseStatus._
import io.netty.handler.codec.http.{FullHttpRequest, HttpResponse, HttpResponseStatus}
import jp.co.cyberagent.aeromock._
import jp.co.cyberagent.aeromock.api.ApiResolver
import jp.co.cyberagent.aeromock.config.Project
import scaldi.Injector

class AeromockApiHttpRequestProcessor(implicit inj: Injector) extends HttpRequestProcessor with HttpResponseWriter {

  val project = inject[Project]

  override def process(rawRequest: FullHttpRequest)(implicit context: ChannelHandlerContext): HttpResponse = {

    inject[ApiResolver].dispatch(rawRequest) match {
      case data => {
        try {
          renderJson(Map("status" -> OK.code()) ++ data, OK)
        } catch {
          case e: AeromockApiBadRequestException => renderJson(Map("status" -> BAD_REQUEST.code(), "message" -> e.getMessage()), BAD_REQUEST)
          case e: AeromockApiNotFoundException => renderJson(createNotFound, HttpResponseStatus.NOT_FOUND)
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
    }
  }

  private def createNotFound: Map[String, Any] = {
    Map(
      "status" -> HttpResponseStatus.NOT_FOUND.code(),
      "message" -> "endpoint not found.")
  }

}
