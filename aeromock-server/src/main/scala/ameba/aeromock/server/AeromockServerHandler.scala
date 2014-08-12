package ameba.aeromock.server

import java.net.InetSocketAddress
import java.util.Locale

import ameba.aeromock.config.ConfigHolder
import ameba.aeromock.core.http.HttpRequestProcessor
import ameba.aeromock.data.ReturnValueStore
import ameba.aeromock.server.http.{HttpRequestProcessorSelector, ServerExceptionHandler}
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

    ReturnValueStore.initialize
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
