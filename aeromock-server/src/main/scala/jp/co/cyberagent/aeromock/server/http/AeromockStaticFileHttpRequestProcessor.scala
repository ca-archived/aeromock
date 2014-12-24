package jp.co.cyberagent.aeromock.server.http

import java.nio.channels.Channels

import io.netty.channel.{ChannelFutureListener, ChannelHandlerContext, ChannelProgressiveFuture, ChannelProgressiveFutureListener}
import io.netty.handler.codec.http.HttpHeaders.Names._
import io.netty.handler.codec.http.HttpHeaders._
import io.netty.handler.codec.http.HttpMethod._
import io.netty.handler.codec.http.HttpResponseStatus._
import io.netty.handler.codec.http.HttpVersion._
import io.netty.handler.codec.http._
import io.netty.handler.stream.ChunkedNioStream
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.{AeromockMethodNotAllowedException, AeromockResourceNotFoundException}
import scaldi.Injector

class AeromockStaticFileHttpRequestProcessor(implicit inj: Injector) extends HttpRequestProcessor with HttpResponseWriter {

  override def process(rawRequest: FullHttpRequest)
    (implicit context: ChannelHandlerContext): HttpResponse = {

    val request = rawRequest.toAeromockRequest(Map.empty)

    if (request.method != GET) {
      throw new AeromockMethodNotAllowedException(request.method, request.url)
    }

    val filePath = request.url match {
      case "/favicon.ico" => "public/favicon.ico"
      case url => url.replaceFirst("/aeromock", "public")
    }

    val is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath)

    if (is == null) {
      throw new AeromockResourceNotFoundException(request.url)
    }

    val response = new DefaultHttpResponse(HTTP_1_1, OK);
    setContentLength(response, is.available())

    setStaticResponseHeader(response, getExtension(filePath))

    if (isKeepAlive(response)) {
      response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE)
    }

    context.write(response)

    val future = context.write(new ChunkedNioStream(Channels.newChannel(is)), context.newProgressivePromise())
    future.addListener(new ChannelProgressiveFutureListener() {
      override def operationProgressed(future: ChannelProgressiveFuture, progress: Long, total: Long) {
      }
      override def operationComplete(future: ChannelProgressiveFuture) {
        is.close()
      }
    })

    val lastContentFuture = context.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT)
    if (!isKeepAlive(rawRequest)) {
      lastContentFuture.addListener(ChannelFutureListener.CLOSE)
    }

    response
  }
}
