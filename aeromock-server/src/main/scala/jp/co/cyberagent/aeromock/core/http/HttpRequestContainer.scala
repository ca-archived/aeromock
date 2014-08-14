package jp.co.cyberagent.aeromock.core.http

import io.netty.handler.codec.http.FullHttpRequest

case class HttpRequestContainer(
  original: FullHttpRequest,
  rewrited: Option[FullHttpRequest]
  ) {


  def finalizedRequest: FullHttpRequest = {
    rewrited match {
      case Some(request) => request
      case None => original
    }
  }

}
