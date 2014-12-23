package jp.co.cyberagent.aeromock.api.controller

import jp.co.cyberagent.aeromock.api.AeromockApiController
import jp.co.cyberagent.aeromock.config.Project
import scaldi.Injector
import scalaz._
import Scalaz._

/**
 *
 * @author stormcat24
 */
class DirectoryController(implicit val injector: Injector) extends AeromockApiController {

  get("/aeromock/api/:context/:dir/list") { request =>

    val project = inject[Project]
    val contexts = project._template.contexts

    val context = request.routeParameters.get("context").getOrElse("localhost")
    contexts.find(_.domain == context) match {
      case Some(c) =>
      case None =>
    }

    Map()
  }
}
