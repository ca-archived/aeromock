package jp.co.cyberagent.aeromock.server

import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType
import io.netty.handler.codec.http.{LastHttpContent, FullHttpRequest, HttpObject, HttpContent}
import io.netty.handler.codec.http.multipart.{Attribute, InterfaceHttpData, HttpPostRequestDecoder}
import io.netty.util.CharsetUtil
import jp.co.cyberagent.aeromock.server.http.ServerExceptionHandler
import scaldi.{Injectable, Injector}

/**
 *
 * @author stormcat24
 */
class AeromockHttpContentHandler(implicit inj: Injector) extends SimpleChannelInboundHandler[HttpObject] with Injectable {

  var decoder: HttpPostRequestDecoder = _

  override def channelRead0(context: ChannelHandlerContext, httpObject: HttpObject): Unit = {

    httpObject match {
      case request: FullHttpRequest =>
        decoder = new HttpPostRequestDecoder(request)
        decoder.setDiscardThreshold(0)
      case chunk: HttpContent =>
        decoder.offer(chunk)
        readChunkByChunk(context)

        chunk match {
          case c: LastHttpContent =>
          case _ =>
            readChunkByChunk(context)
//            sendResponse(ctx);
//            resetPostRequestDecoder();
        }

      case r => println(r)
    }
  }

  private def readChunkByChunk(context: ChannelHandlerContext): Unit = {
    while (decoder.hasNext) {
      val data = decoder.next
      if (data != null) {
        processChunk(context, data)
      }
    }
  }

  private def processChunk(context: ChannelHandlerContext, data: InterfaceHttpData): Unit = {
    data.getHttpDataType match {
      case HttpDataType.Attribute => {
        val attribute = data.asInstanceOf[Attribute]
        if (attribute.getName == "json") {
          val readData = attribute.getByteBuf.toString(CharsetUtil.UTF_8)
          println(readData)
        }
      }
      case _ =>
    }
  }

  override def exceptionCaught(context: ChannelHandlerContext, cause: Throwable) {
    implicit val con = context

    if (context.channel().isActive()) {
      ServerExceptionHandler.handle(cause)
    }

  }
}
