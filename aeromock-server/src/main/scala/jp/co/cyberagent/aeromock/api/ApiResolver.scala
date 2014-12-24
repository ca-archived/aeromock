package jp.co.cyberagent.aeromock.api

import io.netty.handler.codec.http.FullHttpRequest
import jp.co.cyberagent.aeromock.AeromockApiNotFoundException
import jp.co.cyberagent.aeromock.core.http.AeromockHttpRequest
import jp.co.cyberagent.aeromock.helper._
import scaldi.{Injectable, Injector}

/**
 *
 * @author stormcat24
 */
class ApiResolver(implicit inj: Injector) extends Injectable {

  val controllers = inject[Seq[AeromockApiController]]('controllers)

  def dispatch(rawRequest: FullHttpRequest) : Map[String, Any] = {
    val result = controllers.flatMap(api => {
      api.routes.findRoute(rawRequest.getMethod, rawRequest.requestUri)
    })

    if (result.isEmpty) {
      throw new AeromockApiNotFoundException("not found")
    } else {
      val tuple = result.head
      tuple._1(rawRequest.toAeromockRequest(tuple._2))
    }
  }

}
