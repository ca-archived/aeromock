package ameba.aeromock.server.http

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.{HttpResponse, FullHttpRequest}
import ameba.aeromock.config.entity.Project
import ameba.aeromock.config.ConfigHolder

/**
 * Base class of request processing.
 * @author stormcat24
 */
abstract class HttpRequestProcessor {

  /**
   *
   * @param request [[io.netty.handler.codec.http.FullHttpRequest]]
   * @param context [[io.netty.channel.ChannelHandlerContext]]
   * @return [[io.netty.handler.codec.http.HttpResponse]]
   */
  def delegate(request: FullHttpRequest)(implicit context: ChannelHandlerContext): HttpResponse = {
    process(ConfigHolder.getProject, request)
  }

  /**
   *
   * @param setting [[ameba.aeromock.config.entity.Project]]
   * @param request [[io.netty.handler.codec.http.FullHttpRequest]]
   * @param context [[io.netty.channel.ChannelHandlerContext]]
   * @return [[io.netty.handler.codec.http.HttpResponse]]
   */
  def process(setting: Project, request: FullHttpRequest)(implicit context: ChannelHandlerContext): HttpResponse

}
