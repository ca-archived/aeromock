package jp.co.cyberagent.aeromock.server.http

import io.netty.handler.codec.http.HttpResponseStatus

case class RenderResult[A](content: A, response: Option[CustomResponse], debug: Boolean)

case class CustomResponse(code: Int, headers: Map[String, String]) {
  def getResponseStatus: HttpResponseStatus = HttpResponseStatus.valueOf(code)
}
