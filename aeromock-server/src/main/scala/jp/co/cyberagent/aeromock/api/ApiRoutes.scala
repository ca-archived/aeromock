package jp.co.cyberagent.aeromock.api

import io.netty.handler.codec.http.HttpMethod
import jp.co.cyberagent.aeromock.core.http.AeromockHttpRequest
import org.apache.commons.lang3.StringUtils

/**
 *
 * @author stormcat24
 */
class ApiRoutes {

  var routes = List.empty[ApiMeta]

  def addRoute(method: HttpMethod, endpoint: String, callback: AeromockHttpRequest => Map[String, Any]): Unit = {

    val tokens = endpoint.split("/").filter(s => StringUtils.isNotBlank(s)).toSeq

    val routeRegex = """^:(\w+)$""".r
    val routePath = tokens.foldLeft(("^", Seq.empty[String]))((left, right) => {
      routeRegex.findFirstMatchIn(right) match {
        case Some(m) => (left._1 + """/(\w+)""", left._2 ++ Seq(m.group(1)))
        case None => (left._1 + """/""" + right, left._2)
      }
    })

    routes = ApiMeta(method, endpoint, (routePath._1 + "$").r, routePath._2, callback) :: routes
  }

  def findRoute(method: HttpMethod, url: String): Option[(AeromockHttpRequest => Map[String, Any], Map[String, String])] = {
    routes.collectFirst {
      case ApiMeta(method, _, regex, routeParamNames, callback) if regex.pattern.matcher(url).matches => {
        val routeParameters = regex.findFirstMatchIn(url) match {
          case Some(m) =>
            (for (groupIndex <- 1 to m.groupCount) yield routeParamNames(groupIndex -1) -> m.group(groupIndex)).toMap
          case _ => Map.empty[String, String]
        }
        (callback, routeParameters)
      }
    }
  }
}
