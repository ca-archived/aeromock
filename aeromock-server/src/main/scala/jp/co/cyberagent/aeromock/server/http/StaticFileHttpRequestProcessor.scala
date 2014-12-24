package jp.co.cyberagent.aeromock.server.http

import java.io.RandomAccessFile
import java.nio.file.Path

import io.netty.channel._
import io.netty.handler.codec.http.HttpHeaders.Names._
import io.netty.handler.codec.http.HttpHeaders._
import io.netty.handler.codec.http.HttpMethod._
import io.netty.handler.codec.http.HttpResponseStatus.OK
import io.netty.handler.codec.http.HttpVersion._
import io.netty.handler.codec.http._
import jp.co.cyberagent.aeromock.config.Project
import jp.co.cyberagent.aeromock.core.http.AeromockHttpRequest
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.{AeromockMethodNotAllowedException, AeromockResourceNotFoundException}
import org.slf4j.LoggerFactory
import scaldi.Injector

abstract class StaticFileHttpRequestProcessor extends HttpRequestProcessor with HttpResponseWriter {

  val project: Project

  override def process(rawRequest: FullHttpRequest)(implicit context: ChannelHandlerContext): HttpResponse = {

    val request = rawRequest.toAeromockRequest(Map.empty)

    if (request.method != GET) {
      throw new AeromockMethodNotAllowedException(request.method, request.url)
    }

    val target = getStaticFile(project, request)
    if (!target.exists() || target.isDirectory()) {
      throw new AeromockResourceNotFoundException(request.url)
    }

    val raf = new RandomAccessFile(target.toFile(), "r")
    val fileLength = raf.length()

    val response = new DefaultHttpResponse(HTTP_1_1, OK);
    setContentLength(response, fileLength)
    setStaticResponseHeader(response, rawRequest.extension)
    if (isKeepAlive(response)) {
      response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE)
    }

    context.write(response)
    context.write(new DefaultFileRegion(raf.getChannel, 0, fileLength))

    val lastContentFuture = context.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT)
    if (!isKeepAlive(rawRequest)) {
      lastContentFuture.addListener(ChannelFutureListener.CLOSE)
    }

    response
  }

  protected def getStaticFile(project: Project, parsedRequest: AeromockHttpRequest): Path

}
