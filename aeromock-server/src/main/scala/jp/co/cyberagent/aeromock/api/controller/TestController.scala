package jp.co.cyberagent.aeromock.api.controller

import jp.co.cyberagent.aeromock.api.AeromockApiController
import scaldi.Injector

/**
 *
 * @author stormcat24
 */
class TestController(implicit val injector: Injector) extends AeromockApiController {

  get("/aeromock/api/:host/aaa") { request =>
    val host = request.routeParameters.get("host")
    Map("neko" -> "nuko1")
  }

}
