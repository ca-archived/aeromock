package jp.co.cyberagent.aeromock.server.http

import jp.co.cyberagent.aeromock.util.ContentTypeUtil
import io.netty.buffer.Unpooled
import io.netty.channel.{ChannelFutureListener, ChannelHandlerContext}
import io.netty.handler.codec.http.HttpHeaders.Names._
import io.netty.handler.codec.http.{DefaultFullHttpResponse, HttpResponse, HttpResponseStatus}
import io.netty.handler.codec.http.HttpResponseStatus._
import io.netty.handler.codec.http.HttpVersion.HTTP_1_1
import io.netty.util.CharsetUtil
import org.joda.time.DateTime
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write

trait HttpResponseWriter {

  implicit val formats = Serialization.formats(NoTypeHints)

  def redirect(uri: String)(implicit context: ChannelHandlerContext): HttpResponse = {
    val response = new DefaultFullHttpResponse(HTTP_1_1, FOUND)
    addDefaultHeader(response)
    response.headers().set(LOCATION, uri)
    context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
    response
  }

  def renderYaml(content: String, status: HttpResponseStatus)(implicit context: ChannelHandlerContext): HttpResponse = {
    val response = new DefaultFullHttpResponse(HTTP_1_1, status,
      Unpooled.copiedBuffer(content, CharsetUtil.UTF_8))
    addDefaultHeader(response)
    response.headers().set(CONTENT_TYPE, "text/yaml; charset=UTF-8")
    response.headers().set(CONTENT_LENGTH, response.content().readableBytes())

    context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
    response
  }

  def renderHtml(content: String, status: HttpResponseStatus, customResponse: Option[CustomResponse])(implicit context: ChannelHandlerContext): HttpResponse = {
    val responseStatus = selectResponseStatus(status, customResponse)
    val response = new DefaultFullHttpResponse(HTTP_1_1, responseStatus,
      Unpooled.copiedBuffer(content, CharsetUtil.UTF_8))
    addDefaultHeader(response)
    addCustomResponse(response, customResponse)
    response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8")
    response.headers().set(CONTENT_LENGTH, response.content().readableBytes())

    context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
    response
  }

  def renderProtobuf(data: Array[Byte], status: HttpResponseStatus, customResponse: Option[CustomResponse])(implicit context: ChannelHandlerContext): HttpResponse = {
    val responseStatus = selectResponseStatus(status, customResponse)
    val response = new DefaultFullHttpResponse(HTTP_1_1, responseStatus, Unpooled.copiedBuffer(data))
    addDefaultHeader(response)
    addCustomResponse(response, customResponse)
    response.headers().set(CONTENT_TYPE, "application/x-protobuf")
    response.headers().set(CONTENT_LENGTH, response.content().readableBytes())

    context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
    response
  }

  private def selectResponseStatus(status: HttpResponseStatus, customResponse: Option[CustomResponse]): HttpResponseStatus = {
    if (customResponse.isDefined) customResponse.get.getResponseStatus else status
  }

  private def addCustomResponse(response:DefaultFullHttpResponse, customResponse: Option[CustomResponse] = None) {
    customResponse match {
      case Some(value) => value.headers.foreach(header => response.headers().set(header._1, header._2))
      case None =>
    }
  }

  def renderJson[A <: AnyRef](value: A, status: HttpResponseStatus, customResponse: Option[CustomResponse] = None)(implicit context: ChannelHandlerContext): HttpResponse = {
    val responseStatus = selectResponseStatus(status, customResponse)
    val response = new DefaultFullHttpResponse(HTTP_1_1, responseStatus,
      Unpooled.copiedBuffer(write(value), CharsetUtil.UTF_8))
    addDefaultHeader(response)
    addCustomResponse(response, customResponse)
    response.headers().set(CONTENT_TYPE, "application/json; charset=UTF-8")
    response.headers().set(ACCESS_CONTROL_ALLOW_ORIGIN, "*")
    response.headers().set(CONTENT_LENGTH, response.content().readableBytes())

    context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
    response
  }

  def renderResource(content: String, status: HttpResponseStatus)(implicit context: ChannelHandlerContext): HttpResponse = {
    val response = new DefaultFullHttpResponse(HTTP_1_1, status,
      Unpooled.copiedBuffer(content, CharsetUtil.UTF_8))
    addDefaultHeader(response)
    response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8")
    response.headers().set(CONTENT_LENGTH, response.content().readableBytes())

    context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
    response
  }

  def setStaticResponseHeader(response: HttpResponse, extension: Option[String]) {
    addDefaultHeader(response)
    val contentType = extension match {
      case None => None
      case Some(e) => {
        ContentTypeUtil.getContentType(e)
      }
    }

    contentType match {
      case Some(c) => response.headers().set(CONTENT_TYPE, c)
      case None =>
    }
  }

  protected def addDefaultHeader(response: HttpResponse) {
    response.headers().set(CACHE_CONTROL, "no-cache")
    response.headers().set(DATE, DateTime.now().toString("EEE, dd MMM yyy HH:mm:ss z"))
    response.headers().set(SERVER, "Aeromock")
  }

}
