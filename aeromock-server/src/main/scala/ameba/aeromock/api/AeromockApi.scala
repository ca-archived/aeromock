package ameba.aeromock.api

import ameba.aeromock.api.controller.AeromockApiController
import ameba.aeromock.api.controller.DataCreateController
import io.netty.handler.codec.http.HttpMethod

sealed abstract class AeromockApi(
  val endpoint: String,
  val method: HttpMethod,
  val controller: AeromockApiController) {
}

object AeromockApi {

  val ENDPOINT_PREFIX = "/aeromock/api"

  case object DATA_CREATE extends AeromockApi("/data/create", HttpMethod.POST, DataCreateController)

  val values = Array[AeromockApi](DATA_CREATE)

}
