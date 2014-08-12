package ameba.aeromock.core.http

import io.netty.handler.codec.http.FullHttpRequest
import java.util.regex.Pattern
import java.net.URLDecoder

class HttpPlaceHolder(request: FullHttpRequest) {

  def get(key: String): Option[String] = {
    require(key != null)

    HttpStatusKey.values.find(_.key == Symbol(key)) match {
      case None => None
      case Some(result) => result.strategy.fetch(request)
    }
  }
}

sealed abstract class HttpStatusKey(val key: Symbol, val strategy: HttpPlaceHolderStrategy)

object HttpStatusKey {

  case object HTTP_USER_AGENT extends HttpStatusKey('$http_user_agent, HttpUserAgentStrategy)
  case object HTTP_REFERER extends HttpStatusKey('$http_referer, HttpRefererStrategy)
  case object REQUEST_METHOD extends HttpStatusKey('$request_method, RequestMethodStrategy)
  case object REQUEST_URI extends HttpStatusKey('$request_uri, RequestUriStrategy)
  case object QUERY_STRING extends HttpStatusKey('$querystring, QueryStringStrategy)

  val values = Array[HttpStatusKey](
    HTTP_USER_AGENT,
    HTTP_REFERER,
    REQUEST_METHOD,
    REQUEST_URI,
    QUERY_STRING)
}

sealed abstract class HttpPlaceHolderStrategy {
  def fetch(request: FullHttpRequest): Option[String]
}

object HttpUserAgentStrategy extends HttpPlaceHolderStrategy {
  override def fetch(request: FullHttpRequest): Option[String] = {
    request.headers().get("User-Agent") match {
      case null => None
      case result => Some(result)
    }
  }
}

object HttpRefererStrategy extends HttpPlaceHolderStrategy {
  override def fetch(request: FullHttpRequest): Option[String] = {
    request.headers().get("Referer") match {
      case null => None
      case result => Some(result)
    }
  }
}

object RequestMethodStrategy extends HttpPlaceHolderStrategy {
  override def fetch(request: FullHttpRequest): Option[String] = {
    Some(request.getMethod().name())
  }
}

object RequestUriStrategy extends HttpPlaceHolderStrategy {
  override def fetch(request: FullHttpRequest): Option[String] = {
    val decoded = URLDecoder.decode(request.getUri(), "UTF-8")
    Some(decoded.split("?")(0))
  }
}

object QueryStringStrategy extends HttpPlaceHolderStrategy {
  val pattern = Pattern.compile("""\?(.+)$""")
  override def fetch(request: FullHttpRequest): Option[String] = {
    val decoded = URLDecoder.decode(request.getUri(), "UTF-8")
    val matcher = pattern.matcher(decoded)
    if (matcher.find()) {
      Some(matcher.group(1))
    } else {
      None
    }
  }
}