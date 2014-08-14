package jp.co.cyberagent.aeromock.server

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpRequestDecoder
import io.netty.handler.codec.http.HttpResponseEncoder
import io.netty.handler.stream.ChunkedWriteHandler

class AeromockServerInitializer extends ChannelInitializer[SocketChannel] {

  override def initChannel(channel: SocketChannel) {
    channel.pipeline()
      .addLast("decoder", new HttpRequestDecoder)
      .addLast("aggregator", new HttpObjectAggregator(65536))
      .addLast("encoder", new HttpResponseEncoder)
      .addLast("chunkedWriter", new ChunkedWriteHandler)
      .addLast("handler", new AeromockServerHandler(true))
  }
}
