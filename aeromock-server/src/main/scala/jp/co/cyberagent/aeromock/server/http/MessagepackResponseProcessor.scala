package jp.co.cyberagent.aeromock.server.http

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.{HttpResponseStatus, HttpResponse, FullHttpRequest}
import jp.co.cyberagent.aeromock.config.Project
import jp.co.cyberagent.aeromock.helper.DeepTraversal._
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.msgpack.MessagepackResponseService
import org.yaml.snakeyaml.{Yaml, DumperOptions}
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
   * @param request [[FullHttpRequest]]
   * @param context [[ChannelHandlerContext]]
   * @return [[HttpResponse]]
   */
  override def process(request: FullHttpRequest)(implicit context: ChannelHandlerContext): HttpResponse = {
    if (request.queryString.contains(s"${project._naming.debug}=true")) {
      val dumperOptions = new DumperOptions
      dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW)

      val response = createResponseData(project, request.parsedRequest)
      renderYaml(new Yaml(dumperOptions).dumpAsMap(asJavaMap(response._1)(nop)), HttpResponseStatus.OK)
    } else {

      val result = MessagepackResponseService.render(request)
      renderMessagepack(result.content, HttpResponseStatus.OK, result.response)
    }
  }
}
