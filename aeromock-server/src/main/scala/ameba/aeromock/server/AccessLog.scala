package ameba.aeromock.server

import io.netty.handler.codec.http.{HttpHeaders, HttpRequest}
import org.slf4j.LoggerFactory

/**
 *
 * @author stormcat24
 */
object AccessLog {

  val LOG = LoggerFactory.getLogger("access")

  type RequestInfo = (HttpRequest, Long)
  val threadLocal = new ThreadLocal[RequestInfo]

  def initialize(request: HttpRequest) {
    threadLocal.remove()
    threadLocal.set((request, System.currentTimeMillis()))
  }

  def writeAccessLog(statusCode: Int) {
    val requestInfo = threadLocal.get()
    val request = requestInfo._1
    val protocol = request.getProtocolVersion().protocolName()
    val uri = request.getUri()
    val headers = request.headers()
    val ua = headers.get(HttpHeaders.Names.USER_AGENT)
    val time = (System.currentTimeMillis - requestInfo._2).toDouble / 1000
    LOG.info(s"$protocol\t$uri\t$statusCode\t$ua\t$time")
  }
}
