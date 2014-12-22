package jp.co.cyberagent.aeromock.api

import io.netty.handler.codec.http.HttpMethod
import jp.co.cyberagent.aeromock.core.http.AeromockHttpRequest

import scala.util.matching.Regex


/**
 *
 * @author stormcat24
 */
trait AeromockApiController {

  import HttpMethod._

  val routes = new ApiRoutes

  def get(endpoint: String)(callback: AeromockHttpRequest => Map[String, Any]): Unit = routes.addRoute(GET, endpoint, callback)
  def post(endpoint: String)(callback: AeromockHttpRequest => Map[String, Any]): Unit = routes.addRoute(POST, endpoint, callback)
  def put(endpoint: String)(callback: AeromockHttpRequest => Map[String, Any]): Unit = routes.addRoute(PUT, endpoint, callback)
  def delete(endpoint: String)(callback: AeromockHttpRequest => Map[String, Any]): Unit = routes.addRoute(DELETE, endpoint, callback)

}


case class ApiMeta(
  method: HttpMethod,
  endpoint: String,
  regex: Regex,
  routeParamNames: Seq[String],
  callback: AeromockHttpRequest => Map[String, Any]
)
