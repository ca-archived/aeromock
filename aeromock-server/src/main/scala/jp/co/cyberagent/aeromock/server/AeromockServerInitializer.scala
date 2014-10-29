package jp.co.cyberagent.aeromock.server

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpRequestDecoder
import io.netty.handler.codec.http.HttpResponseEncoder
import io.netty.handler.codec.protobuf.ProtobufEncoder
import io.netty.handler.stream.ChunkedWriteHandler
import scaldi.Injector

class AeromockServerInitializer(implicit inj: Injector) extends ChannelInitializer[SocketChannel] {

  override def initChannel(channel: SocketChannel) {
    channel.pipeline()
      .addLast(classOf[HttpRequestDecoder].getName, new HttpRequestDecoder)
      .addLast(classOf[HttpObjectAggregator].getName, new HttpObjectAggregator(65536))
      .addLast(classOf[HttpResponseEncoder].getName, new HttpResponseEncoder)
      .addLast(classOf[ProtobufEncoder].getName, new ProtobufEncoder)
      .addLast(classOf[ChunkedWriteHandler].getName, new ChunkedWriteHandler)
      .addLast(classOf[AeromockServerHandler].getName, new AeromockServerHandler(true))
  }
}
