package jp.co.cyberagent.aeromock.api

import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpMethod._
import jp.co.cyberagent.aeromock.api.controller.{AeromockApiController, DataCreateController}
import jp.co.cyberagent.aeromock.core.http.ParsedRequest
import scaldi.{Injectable, Injector}

sealed abstract class AeromockApi(
  val endpoint: String,
  val method: HttpMethod,
  val controller: AeromockApiController) {
}

object AeromockApi extends Injectable {

  def fetchController(request: ParsedRequest)(implicit inj: Injector): Option[AeromockApiController] = {
    request match {
      case ParsedRequest(_, _, _, GET) => Some(inject[DataCreateController])
      case _ => None
    }
  }

}
