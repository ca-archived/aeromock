package jp.co.cyberagent.aeromock.template

import java.io.Writer

import groovy.lang.Binding
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpHeaders.Names
import jp.co.cyberagent.aeromock.AeromockRenderException
import jp.co.cyberagent.aeromock.config.Project
import jp.co.cyberagent.aeromock.core.http.{Endpoint, ParsedRequest, VariableManager}
import jp.co.cyberagent.aeromock.core.script.GroovyScriptRunner
import jp.co.cyberagent.aeromock.data._
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.server.http.{RenderResult, ResponseDataSupport}
import jp.co.cyberagent.aeromock.util.DummyWriter
import org.joda.time.DateTime
import org.yaml.snakeyaml.{DumperOptions, Yaml}
import scaldi.{Injectable, Injector}


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
        case Left(e) => throw new AeromockRenderException(request.parsedRequest.url, e.getCause)
        case Right(result) => result
      }
    }
  }

  def renderProcess(request: ParsedRequest): Either[Throwable, RenderResult[String]] = {
    val response = createResponseDataWithProjection(project, request)

    trye {
      try {
        RenderResult(renderHtml(request, response._1), response._2, false)
      } catch {
        case e: Throwable => {
          throw new AeromockRenderException("rendering error", e)
        }
      }
    }
  }

  protected def renderHtml(request: ParsedRequest, projection: InstanceProjection): String

  def validateData(endpoint: Endpoint, domain: String)
      (reporting: PageValidation => Unit = {v: PageValidation =>}): PageValidation = {
    require(endpoint != null)
    require(domain != null)

    val templatePath = endpoint.raw + extension
    val validationResult = templateAssertProcess(templatePath) match {
      case Left(result) => PageValidation(templatePath, result, None)
      case Right(templateProcess) => {
        val rootStartTimemills = System.currentTimeMillis()

        val dataValidates = DataFileService.getRelatedDataFiles(endpoint).map(dataFile => {
          val relativeDataPath = dataFile.path.getRelativePath(project._data.root).toString
          val startTimemills = System.currentTimeMillis()
          trye {

            val imitatedRequest = dataFile match {
              case DataFile(None, _, method) => ParsedRequest(endpoint.value, Map.empty, Map.empty, method)
              case DataFile(Some(id), _, method) => ParsedRequest(endpoint.value, Map("_dataid" -> id), Map.empty, method)
            }

            val namesClass = classOf[Names]
            // substitute value of key for value as dummy value
            val imitatedOriginalMap =
              namesClass.getFields.toArray.map(f => (f.getName, f.getName)).toMap ++
                Map("HOST" -> s"${domain}:${listenPort}")

            // TODO [Technical debt]
            val requestMap = imitatedOriginalMap ++ Map(
              "REQUEST_METHOD" -> "GET",
              "REQUEST_URI" -> endpoint.value,
              "QUERY_STRING" -> "",
              "PARAMETERS" -> Map.empty,
              "FORM_DATA" -> Map.empty,
              "NOW" -> DateTime.now().toDate(),
              "REMOTE_ADDR" -> "REMOTE_ADDR",
              "REMOTE_HOST" -> "REMOTE_HOST"
            )
            VariableManager.initializeRequestMap(requestMap)

            // TODO [Technical debt] not DRY
            val originalVariables = if (project.variableScript.exists()) {
              val scriptRunner = new GroovyScriptRunner[java.util.Map[String, AnyRef]](project.variableScript)
              val binding = new Binding
              requestMap.foreach(pair => binding.setVariable(pair._1, pair._2))
              scriptRunner.run(binding)
            } else {
              new java.util.HashMap[String, AnyRef]
            }
            VariableManager.initializeOriginalVariableMap(originalVariables)

            val response = createResponseDataWithProjection(project, imitatedRequest)
            val proxyMap = response._1.toInstanceJava().asInstanceOf[java.util.Map[_, _]]
            VariableManager.initializeDataMap(proxyMap)

            trye(templateProcess(proxyMap, DummyWriter)) match {
              case Right(_) => DataAssertSuccess(relativeDataPath, getDifferenceSecondsFromNow(startTimemills))
              case Left(value) => {
                DataAssertFailure(relativeDataPath, getDifferenceSecondsFromNow(startTimemills), value.getMessage, Some(value))
              }
            }
          } match {
            case Right(value) => value
            case Left(value) => {
              DataAssertError(relativeDataPath, getDifferenceSecondsFromNow(startTimemills), value.getMessage, Some(value))
            }
          }
        })

        val groupMap = dataValidates.groupBy(_.getAssertionResultType())
        val dataAsserts = DataAsserts(
          groupMap.getOrElse(AssertionResultType.SUCCESS, List.empty).map(_.asInstanceOf[DataAssertSuccess]),
          groupMap.getOrElse(AssertionResultType.FAILURE, List.empty).map(_.asInstanceOf[DataAssertFailure]),
          groupMap.getOrElse(AssertionResultType.ERROR, List.empty).map(_.asInstanceOf[DataAssertError])
        )

        PageValidation(templatePath, TemplateAssertSuccess(getDifferenceSecondsFromNow(rootStartTimemills)), Some(dataAsserts))
      }
    }

    reporting(validationResult)
    validationResult
  }

  def templateAssertProcess(templatePath: String): Either[TemplateAssertResult, (Any, Writer) => Unit]

  /**
   * @return extension
   */
  def extension: String

}

