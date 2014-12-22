package jp.co.cyberagent.aeromock.api.controller

import jp.co.cyberagent.aeromock.api.AeromockNewApi

/**
 *
 * @author stormcat24
 */
class TestController extends AeromockNewApi {

  get("/aeromock/api/:host/aaa") { request =>
    val host = request.routeParameters.get("host")
    println(host)
    Map("neko" -> "nuko1")
  }

}
