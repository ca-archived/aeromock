package jp.co.cyberagent.aeromock.api.controller

import jp.co.cyberagent.aeromock.api.AeromockApiController

/**
 *
 * @author stormcat24
 */
class TestController extends AeromockApiController {

  get("/aeromock/api/:host/aaa") { request =>
    val host = request.routeParameters.get("host")
    println(host)
    Map("neko" -> "nuko1")
  }

}
