package jp.co.cyberagent.aeromock.template

import java.io.Writer

import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpHeaders.Names
import jp.co.cyberagent.aeromock.config.entity.Project
import jp.co.cyberagent.aeromock.config.{ConfigHolder, ServerOptionRepository}
import jp.co.cyberagent.aeromock.core.el.VariableHelper
import jp.co.cyberagent.aeromock.core.http.{Endpoint, ParsedRequest, RequestManager}
import jp.co.cyberagent.aeromock.data._
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.server.http.{CustomResponse, RenderResult, ResponseStatusSupport}
import jp.co.cyberagent.aeromock.util.DummyWriter
import jp.co.cyberagent.aeromock.{AeromockLoadDataException, AeromockNoneRelatedDataException, AeromockRenderException, AeromockSystemException}
import org.joda.time.DateTime
import org.yaml.snakeyaml.{DumperOptions, Yaml}

import scalaz._


/**
 * Base service class of Template.
 * @author stormcat24
 */
abstract class TemplateService extends AnyRef with ResponseStatusSupport {

  val project = ConfigHolder.getProject
  lazy val dataFileService = new DataFileService(project)

  /**
   * Scan template file and data file, then return merged response html data.
   * @param request [[io.netty.handler.codec.http.FullHttpRequest]]
   * @return HTML string
   */
  def render(request: FullHttpRequest): RenderResult = {
    require(request != null)

    if (request.queryString.contains(s"${project._naming.debug}=true")) {
      val dumperOptions = new DumperOptions
      dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW)

      val yaml = new Yaml(dumperOptions)
      val response = createResponseData(project, request.parsedRequest)
      val proxyMap = response._1.toInstanceJava().asInstanceOf[java.util.Map[_, _]]
      RenderResult(yaml.dumpAsMap(proxyMap), response._2, true)
    } else {
      renderProcess(request.parsedRequest) match {
        case Left(e) => throw new AeromockRenderException(request.parsedRequest.url, e.getCause)
        case Right(result) => result
      }
    }
  }

  def renderProcess(request: ParsedRequest): Either[Throwable, RenderResult] = {
    val response = createResponseData(project, request)

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

  /**
   * Create [[jp.co.cyberagent.aeromock.data.InstanceProjection]]
   * @param project [[jp.co.cyberagent.aeromock.config.entity.Project]]
   * @param request [[jp.co.cyberagent.aeromock.core.http.ParsedRequest]]
   * @return [[jp.co.cyberagent.aeromock.data.InstanceProjection]]
   */
  protected def createResponseData(project: Project, request: ParsedRequest): (InstanceProjection, Option[CustomResponse]) = {

    val dataRootDir = project._data.root
    val dataFile = DataPathResolver.resolve(dataRootDir, request) match {
      case None => throw new AeromockNoneRelatedDataException(request.url)
      case Some(file) => file
    }

    val dataMap = DataFileReaderFactory.create(dataFile) match {
      case None => throw new AeromockSystemException(s"Cannot read Data file '${dataFile.toString}'")
      case Some(reader) => {
        reader.readFile(dataFile).collect {
          case (key, value) => (key, value)
        }.toMap
      }
    }

    val commonDataHelper = new CommonDataHelper(project._naming)
    val commonMergedMap = commonDataHelper.getMergedDataMap(dataRootDir, project.dataScript)
    val mergedMap = commonDataHelper.mergeAdditional(commonMergedMap, dataMap)

    val customResponse = createCustomResponse(project._naming, mergedMap) match {
      case Success(value) => value
      case Failure(e) => {
        val errors = e.list.map(_.getMessage)
        throw new AeromockLoadDataException(NonEmptyList(errors.head, errors.drop(1): _*))
      }
    }

    val reducedMap = mergedMap - project._naming.response

    // 関数合成することでasJavaMapの抽象度を保ちつつ、走査のついでに変数置換を行う
    val variableHelper = new VariableHelper(RequestManager.getRequestMap)

    new InstanceProjectionFactory(variableHelper.variableConversion, project._naming).create(reducedMap) match {
      case Failure(errors) => throw new AeromockLoadDataException(errors.map(_.getMessage))
      case Success(p) => (p, customResponse)
    }
  }

  def validateData(endpoint: Endpoint, domain: String)
      (reporting: PageValidation => Unit = {v: PageValidation =>}): PageValidation = {
    require(endpoint != null)
    require(domain != null)

    val templatePath = endpoint.raw + extension
    val validationResult = templateAssertProcess(templatePath) match {
      case Left(result) => PageValidation(templatePath, result, None)
      case Right(templateProcess) => {
        val rootStartTimemills = System.currentTimeMillis()

        val dataValidates = dataFileService.getRelatedDataFiles(endpoint).map(dataFile => {
          val relativeDataPath = dataFile.path.getRelativePath(project._data.root).toString
          val startTimemills = System.currentTimeMillis()
          trye {

            val imitatedRequest = dataFile match {
              case DataFile(None, _) => ParsedRequest(endpoint.value, Map.empty, Map.empty)
              case DataFile(Some(id), _) => ParsedRequest(endpoint.value, Map("_dataid" -> id), Map.empty)
            }

            val namesClass = classOf[Names]
            // substitute value of key for value as dummy value
            val imitatedOriginalMap =
              namesClass.getFields.toArray.map(f => (f.getName, f.getName)).toMap ++
                Map("HOST" -> s"${domain}:${ServerOptionRepository.listenPort}")

            // TODO [Technical debt]
            val imitatedRequestMap = Map(
              "REQUEST_METHOD" -> "GET",
              "REQUEST_URI" -> endpoint.value,
              "QUERY_STRING" -> "",
              "PARAMETERS" -> Map.empty,
              "FORM_DATA" -> Map.empty,
              "NOW" -> DateTime.now().toDate(),
              "REMOTE_ADDR" -> "REMOTE_ADDR",
              "REMOTE_HOST" -> "REMOTE_HOST"
            )
            RequestManager.initializeRequestMap(imitatedOriginalMap ++ imitatedRequestMap)

            val response = createResponseData(project, imitatedRequest)
            val proxyMap = response._1.toInstanceJava().asInstanceOf[java.util.Map[_, _]]
            RequestManager.initializeDataMap(proxyMap)

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

