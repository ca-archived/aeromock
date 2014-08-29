package jp.co.cyberagent.aeromock.server.http

import io.netty.handler.codec.http._
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.HttpHeaders.Names._
import io.netty.handler.codec.http.HttpHeaders._
import io.netty.handler.codec.http.HttpMethod._
import io.netty.handler.codec.http.HttpResponseStatus._
import io.netty.handler.codec.http.HttpVersion._
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelProgressiveFutureListener
import io.netty.channel.ChannelProgressiveFuture
import jp.co.cyberagent.aeromock.config.Project
import jp.co.cyberagent.aeromock.{AeromockResourceNotFoundException, AeromockMethodNotAllowedException}
import org.slf4j.LoggerFactory
import io.netty.handler.stream.ChunkedNioStream
import java.nio.channels.Channels
import jp.co.cyberagent.aeromock.helper._

object AeromockStaticFileHttpRequestProcessor extends HttpRequestProcessor with HttpResponseWriter {

  val LOG = LoggerFactory.getLogger(AeromockStaticFileHttpRequestProcessor.this.getClass())

  override def process(project: Project, request: FullHttpRequest)
    (implicit context: ChannelHandlerContext): HttpResponse = {

    if (request.getMethod() != GET) {
      throw new AeromockMethodNotAllowedException(request.getMethod(), request.parsedRequest.url)
    }

    val filePath = request.parsedRequest.url match {
      case "/favicon.ico" => "public/favicon.ico"
      case url => url.replaceFirst("/aeromock", "public")
    }

    val is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath)

    if (is == null) {
      throw new AeromockResourceNotFoundException(request.parsedRequest.url)
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
    if (!isKeepAlive(request)) {
      lastContentFuture.addListener(ChannelFutureListener.CLOSE)
    }

    response
  }
}
