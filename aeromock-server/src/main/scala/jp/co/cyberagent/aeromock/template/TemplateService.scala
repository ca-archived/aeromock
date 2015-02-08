package jp.co.cyberagent.aeromock.template

import io.netty.handler.codec.http.FullHttpRequest
import jp.co.cyberagent.aeromock.AeromockRenderException
import jp.co.cyberagent.aeromock.config.Project
import jp.co.cyberagent.aeromock.core.http.ParsedRequest
import jp.co.cyberagent.aeromock.data._
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.server.http.{RenderResult, ResponseDataSupport}
import org.yaml.snakeyaml.{DumperOptions, Yaml}
import scaldi.{Injectable, Injector}

import scalaz.Scalaz._
import scalaz._


/**
 * Base service class of Template.
 * @author stormcat24
 */
trait TemplateService extends AnyRef with ResponseDataSupport with Injectable {

  implicit val inj: Injector
  val project: Project = inject[Project]
  val listenPort = inject[Int](identified by 'listenPort)

  /**
   * Scan template file and data file, then return merged response html data.
   * @param request [[io.netty.handler.codec.http.FullHttpRequest]]
   * @return HTML string
   */
  def render(request: FullHttpRequest): RenderResult[String] = {
    require(request != null)

    if (request.queryString.contains(s"${project._naming.debug}=true")) {
      val dumperOptions = new DumperOptions
      dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW)

      val response = createResponseDataWithProjection(project, request.parsedRequest)
      val proxyMap = response._1.toInstanceJava().asInstanceOf[java.util.Map[_, _]]
      RenderResult(new Yaml(dumperOptions).dumpAsMap(proxyMap), response._2, true)
    } else {
      renderProcess(request.parsedRequest) match {
        case -\/(e) => throw new AeromockRenderException(request.parsedRequest.url, e.getCause)
        case \/-(result) => result
      }
    }
  }

  def renderProcess(request: ParsedRequest): \/[Throwable, RenderResult[String]] = {
    val response = createResponseDataWithProjection(project, request)
    \/.fromTryCatchNonFatal(renderHtml(request, response._1)).rightMap(RenderResult(_, response._2, false))
  }

  protected def renderHtml(request: ParsedRequest, projection: InstanceProjection): String

  /**
   * @return extension
   */
  def extension: String

}

