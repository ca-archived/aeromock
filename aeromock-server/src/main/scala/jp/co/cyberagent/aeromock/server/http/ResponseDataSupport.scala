package jp.co.cyberagent.aeromock.server.http

import jp.co.cyberagent.aeromock.core.el.VariableHelper
import jp.co.cyberagent.aeromock.{AeromockLoadDataException, AeromockSystemException, AeromockNoneRelatedDataException}
import jp.co.cyberagent.aeromock.config.{Project, Naming}
import jp.co.cyberagent.aeromock.core.http.{VariableManager, ParsedRequest}
import jp.co.cyberagent.aeromock.data._
import jp.co.cyberagent.aeromock.helper._

import scala.collection.JavaConverters._
import scalaz._
import Validation._
import Scalaz._

/**
 *
 * @author stormcat24
 */
trait ResponseDataSupport {

  def createCustomResponse(naming: Naming, dataMap: Map[Any, Any]): ValidationNel[Throwable, Option[CustomResponse]] = {
    dataMap.get(naming.response) match {
      case Some(response) => {
        val map = cast[Map[Any, Any]](response).toValidationNel
        map.flatMap { m =>
          val codeResult = for {
            codeRaw <- m.get("code").toSuccess(new Throwable("'code' not specified"))
            code <- codeRaw |> ((value: Any) => fromTryCatch(value.asInstanceOf[Int]))
          } yield (code)

          val headersResult = m.get("headers") match {
            case None => Map.empty[String, String].success[Throwable]
            case Some(value) => {
              for {
                headers <- value |> ((value: Any) => fromTryCatch(value.asInstanceOf[Map[String, String]]))
              } yield (headers)
            }
          }

          (codeResult.toValidationNel |@| headersResult.toValidationNel) {
            CustomResponse(_, _).some
          }
        }
      }
      case None => none[CustomResponse].successNel[Throwable]
    }
  }

  /**
   * Create [[jp.co.cyberagent.aeromock.data.InstanceProjection]]
   * @param project [[Project]]
   * @param request [[jp.co.cyberagent.aeromock.core.http.ParsedRequest]]
   * @return [[jp.co.cyberagent.aeromock.data.InstanceProjection]]
   */
  protected def createResponseDataWithProjection(project: Project, request: ParsedRequest): (InstanceProjection, Option[CustomResponse]) = {

    val data = createResponseData(project, request)
    // 関数合成することでasJavaMapの抽象度を保ちつつ、走査のついでに変数置換を行う
    val variableHelper = new VariableHelper(VariableManager.getRequestMap ++ VariableManager.getOriginalVariableMap().asScala.toMap)

    new InstanceProjectionFactory(variableHelper, project._naming).create(data._1) match {
      case Failure(errors) => throw new AeromockLoadDataException(errors.map(_.getMessage))
      case Success(p) => (p, data._2)
    }

  }

  protected def createResponseData(project: Project, request: ParsedRequest): (Map[Any, Any], Option[CustomResponse]) = {

    val dataRootDir = project._data.root
    val naming = project._naming
    val dataFile = DataPathResolver.resolve(dataRootDir, request, naming) match {
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

    (mergedMap - project._naming.response, customResponse)
  }

}
