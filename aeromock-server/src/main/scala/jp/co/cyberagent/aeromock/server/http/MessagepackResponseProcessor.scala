package jp.co.cyberagent.aeromock.server.http

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.{FullHttpRequest, HttpResponse, HttpResponseStatus}
import jp.co.cyberagent.aeromock.config.Project
import jp.co.cyberagent.aeromock.helper.DeepTraversal._
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.msgpack.MessagepackResponseService
import org.yaml.snakeyaml.{DumperOptions, Yaml}
import scaldi.Injector

/**
 * [[jp.co.cyberagent.aeromock.server.http.HttpRequestProcessor]] for messagepack.
 * @author stormcat24
 */
class MessagepackResponseProcessor (implicit inj: Injector) extends HttpRequestProcessor
  with HttpResponseWriter with ResponseDataSupport {

  val project = inject[Project]

  /**
   *
   * @param rawRequest [[FullHttpRequest]]
   * @param context [[ChannelHandlerContext]]
   * @return [[HttpResponse]]
   */
  override def process(rawRequest: FullHttpRequest)(implicit context: ChannelHandlerContext): HttpResponse = {

    val request = rawRequest.toAeromockRequest(Map.empty)
    request.queryParameters.get(project._naming.debug) match {
      case Some("true") => {
        val dumperOptions = new DumperOptions
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW)

        val response = createResponseData(project, request)
        renderYaml(new Yaml(dumperOptions).dumpAsMap(asJavaMap(response._1)(nop)), HttpResponseStatus.OK)
      }
      case _ =>
        val result = MessagepackResponseService.render(request)
        renderMessagepack(result.content, HttpResponseStatus.OK, result.response)
    }

  }
}
