package jp.co.cyberagent.aeromock.server.http

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.{FullHttpRequest, HttpResponse, HttpResponseStatus}
import jp.co.cyberagent.aeromock.api.AeromockApi
import jp.co.cyberagent.aeromock.config.Project
import jp.co.cyberagent.aeromock.helper._
import scaldi.Injector

class AeromockApiHttpRequestProcessor(implicit inj: Injector) extends HttpRequestProcessor with HttpResponseWriter {

  val project = inject[Project]

  override def process(request: FullHttpRequest)(implicit context: ChannelHandlerContext): HttpResponse = {

    AeromockApi.fetchController(request.parsedRequest) match {
      case Some(controller) => controller.render(request.parsedRequest)
      case None => renderJson(createNotFound, HttpResponseStatus.NOT_FOUND)
    }
  }

  private def createNotFound: Map[String, Any] = {
    Map(
      "status" -> HttpResponseStatus.NOT_FOUND.code(),
      "message" -> "endpoint not found.")
  }


}
