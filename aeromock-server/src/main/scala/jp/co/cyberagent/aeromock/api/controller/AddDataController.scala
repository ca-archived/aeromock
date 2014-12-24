package jp.co.cyberagent.aeromock.api.controller

import jp.co.cyberagent.aeromock.api.AeromockApiController
import scaldi.Injector

/**
 *
 * @author stormcat24
 */
class AddDataController(implicit val injector: Injector) extends AeromockApiController {

  post("/aeromock/add_data/:context/:path") { request =>

    // TODO
    Map()
  }

}
