package jp.co.cyberagent.aeromock.server

import java.net.InetSocketAddress
import java.util.Locale

import jp.co.cyberagent.aeromock.config.ConfigHolder
import jp.co.cyberagent.aeromock.core.http.HttpRequestProcessor
import jp.co.cyberagent.aeromock.data.DynamicMethodValueStore
import jp.co.cyberagent.aeromock.server.http.{HttpRequestProcessorSelector, ServerExceptionHandler}
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http.FullHttpRequest

class AeromockServerHandler(val useSendFile: Boolean)
  extends SimpleChannelInboundHandler[FullHttpRequest] {

  override def channelRead0(context: ChannelHandlerContext, request: FullHttpRequest) {
    implicit val con = context

    AccessLog.initialize(request)

    ConfigHolder.initialize
    ConfigHolder.getUserConfig.language match {
      case Some(locale) => Locale.setDefault(locale)
      case None =>
    }

    DynamicMethodValueStore.initialize
    val requestContainer = HttpRequestProcessor.execute(request,
      context.channel().remoteAddress().asInstanceOf[InetSocketAddress])
    val finalizedRequest = requestContainer.finalizedRequest

    val delegator = HttpRequestProcessorSelector.select(ConfigHolder.getProject, finalizedRequest)
    val response = delegator.delegate(finalizedRequest)

    AccessLog.writeAccessLog(response.getStatus.code())
  }

  override def exceptionCaught(context: ChannelHandlerContext, cause: Throwable) {
    implicit val con = context

    if (context.channel().isActive()) {
      ServerExceptionHandler.handle(cause)
    }

  }

}
