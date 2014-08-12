package ameba.aeromock.server.http

import scala.Array.canBuildFrom
import ameba.aeromock.api.AeromockApi
import ameba.aeromock.helper._
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.{HttpResponse, FullHttpRequest, HttpResponseStatus}
import ameba.aeromock.config.entity.Project

object AeromockApiHttpRequestProcessor extends HttpRequestProcessor with HttpResponseWriter {

  val apiUrlMap = AeromockApi.values.map(entry => (AeromockApi.ENDPOINT_PREFIX + entry.endpoint -> entry)).toMap

  override def process(project: Project, request: FullHttpRequest)
    (implicit context: ChannelHandlerContext): HttpResponse = {

    apiUrlMap.get(request.parsedRequest.url) match {
      case Some(api) => {
        if (api.method != request.getMethod()) {
          renderJson(createNotFound, HttpResponseStatus.NOT_FOUND)
        } else {
          api.controller.render(request.parsedRequest)
        }
      }
      case None => renderJson(createNotFound, HttpResponseStatus.NOT_FOUND)
    }

  }

  private def createNotFound: Map[String, Any] = {
    Map(
      "status" -> HttpResponseStatus.NOT_FOUND.code(),
      "message" -> "endpoint not found.")
  }


}
