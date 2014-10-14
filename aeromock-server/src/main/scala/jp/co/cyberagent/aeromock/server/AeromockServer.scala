package jp.co.cyberagent.aeromock.server

import org.slf4j.LoggerFactory

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import scaldi.Injector

/**
 * Aeromock Server Daemon
 * @param port listeing port
 * @author stormcat24
 */
class AeromockServer(val port: Int)(implicit inj: Injector) {

  val LOG = LoggerFactory.getLogger(classOf[AeromockServer])
  LOG.info("Aeromock server listening on port {}", port)

  def run() {
    process(new NioEventLoopGroup, new NioEventLoopGroup) {
      (root, worker) =>
        val bootstrap = new ServerBootstrap
        bootstrap.group(root, worker)
          .channel(classOf[NioServerSocketChannel])
          .childHandler(new AeromockServerInitializer)
          .bind(port).sync().channel().closeFuture().sync()
    }
  }

  private def process(root: NioEventLoopGroup, worker: NioEventLoopGroup)(func: (NioEventLoopGroup, NioEventLoopGroup) => Unit) {
    try {
      func(root, worker)
    } finally {
      root.shutdownGracefully()
      worker.shutdownGracefully()
    }
  }
}
