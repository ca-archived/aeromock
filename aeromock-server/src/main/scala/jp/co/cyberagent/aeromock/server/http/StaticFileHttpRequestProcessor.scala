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
import jp.co.cyberagent.aeromock.config.entity.Project
import jp.co.cyberagent.aeromock.core.http.ParsedRequest
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.{AeromockMethodNotAllowedException, AeromockResourceNotFoundException}
import org.slf4j.LoggerFactory

abstract class StaticFileHttpRequestProcessor extends HttpRequestProcessor with HttpResponseWriter {

  val LOG = LoggerFactory.getLogger(StaticFileHttpRequestProcessor.this.getClass())

  override def process(project: Project, request: FullHttpRequest)(implicit context: ChannelHandlerContext): HttpResponse = {

    if (request.getMethod() != GET) {
      throw new AeromockMethodNotAllowedException(request.getMethod(), request.parsedRequest.url)
    }

    val target = getStaticFile(project, request.parsedRequest)
    if (!target.exists() || target.isDirectory()) {
      throw new AeromockResourceNotFoundException(request.parsedRequest.url)
    }

    val raf = new RandomAccessFile(target.toFile(), "r")
    val fileLength = raf.length()

    val response = new DefaultHttpResponse(HTTP_1_1, OK);
    setContentLength(response, fileLength)
    setStaticResponseHeader(response, request.extension)
    if (isKeepAlive(response)) {
      response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE)
    }

    context.write(response)
    context.write(new DefaultFileRegion(raf.getChannel, 0, fileLength))

    val lastContentFuture = context.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT)
    if (!isKeepAlive(request)) {
      lastContentFuture.addListener(ChannelFutureListener.CLOSE)
    }

    response
  }

  protected def getStaticFile(project: Project, parsedRequest: ParsedRequest): Path

}
