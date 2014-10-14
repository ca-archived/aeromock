package jp.co.cyberagent.aeromock.server

import java.net.InetSocketAddress
import java.util.Locale

import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http.FullHttpRequest
import jp.co.cyberagent.aeromock.config.UserConfig
import jp.co.cyberagent.aeromock.core.http.HttpRequestProcessor
import jp.co.cyberagent.aeromock.data.DynamicMethodValueStore
import jp.co.cyberagent.aeromock.server.http.{HttpRequestProcessorSelector, ServerExceptionHandler}
import scaldi.{Injectable, Injector}

class AeromockServerHandler(val useSendFile: Boolean)(implicit inj: Injector)
  extends SimpleChannelInboundHandler[FullHttpRequest] with Injectable {

  override def channelRead0(context: ChannelHandlerContext, request: FullHttpRequest) {
    implicit val con = context

    AccessLog.initialize(request)

    val userConfig = inject[UserConfig]
    userConfig.language.map(Locale.setDefault(_))

    DynamicMethodValueStore.initialize
    val requestContainer = inject[HttpRequestProcessor].execute(request,
      context.channel().remoteAddress().asInstanceOf[InetSocketAddress])
    val finalizedRequest = requestContainer.finalizedRequest

    val delegator = HttpRequestProcessorSelector.select(finalizedRequest)
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
