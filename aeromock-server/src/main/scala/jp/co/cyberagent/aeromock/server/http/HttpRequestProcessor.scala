package jp.co.cyberagent.aeromock.server.http

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.{FullHttpRequest, HttpResponse}
import org.slf4j.LoggerFactory
import scaldi.Injectable

/**
 * Base class of request processing.
 * @author stormcat24
 */
trait HttpRequestProcessor extends AnyRef with Injectable {

  val LOG = LoggerFactory.getLogger(this.getClass)

  /**
   *
   * @param request [[io.netty.handler.codec.http.FullHttpRequest]]
   * @param context [[io.netty.channel.ChannelHandlerContext]]
   * @return [[io.netty.handler.codec.http.HttpResponse]]
   */
  def delegate(request: FullHttpRequest)(implicit context: ChannelHandlerContext): HttpResponse = process(request)

  /**
   *
   * @param request [[io.netty.handler.codec.http.FullHttpRequest]]
   * @param context [[io.netty.channel.ChannelHandlerContext]]
   * @return [[io.netty.handler.codec.http.HttpResponse]]
   */
  def process(request: FullHttpRequest)(implicit context: ChannelHandlerContext): HttpResponse

}
