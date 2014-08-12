package ameba.aeromock.server.http

import ameba.aeromock.AeromockLoadDataException
import ameba.aeromock.config.entity.Project
import ameba.aeromock.core.builtin.BuiltinVariableHelper
import ameba.aeromock.data.CommonDataHelper
import ameba.aeromock.helper.DeepTraversal._

import scalaz.{Failure, NonEmptyList, Success}

/**
 * Trait create response object for JSON API.
 * @author stormcat24
 */
trait JsonApiResponseWriter {

  /**
   * Returns response object.
   * @return response object
   */
  def write(): (AnyRef, Option[CustomResponse])

}

/**
 * [[ameba.aeromock.server.http.JsonApiResponseWriter]] for root element of array object.
 * @param project [[ameba.aeromock.config.entity.Project]]
 * @param variableHelper [[ameba.aeromock.core.builtin.BuiltinVariableHelper]]
 * @param data sequential data
 */
class JsonApiArrayResponseWriter(project: Project, variableHelper: BuiltinVariableHelper, data: Seq[Any]) extends JsonApiResponseWriter {

  // Don't merge common data files, because structur of root element is array.
  /**
   * @inheritdoc
   */
  override def write(): (AnyRef, Option[CustomResponse]) = (scanSeq(data)(variableHelper.variableConversion), None)
}

/**
 * [[ameba.aeromock.server.http.JsonApiResponseWriter]] for root element of map object.
 * @param project [[ameba.aeromock.config.entity.Project]]
 * @param variableHelper [[ameba.aeromock.core.builtin.BuiltinVariableHelper]]
 * @param data map data
 */
class JsonApiMapResponseWriter(project: Project, variableHelper: BuiltinVariableHelper, data: Map[Any, Any])
  extends JsonApiResponseWriter with ResponseStatusSupport{

  /**
   * @inheritdoc
   */
  override def write(): (AnyRef, Option[CustomResponse]) = {

    val commonDataHelper = new CommonDataHelper(project._naming)
    val commonMergeMap = commonDataHelper.getMergedDataMap(project._ajax.root, project.ajaxScript)
    val mergedMap = commonDataHelper.mergeAdditional(commonMergeMap, data)

    val customResponse = createCustomResponse(project._naming, mergedMap) match {
      case Success(value) => value
      case Failure(e) => {
        val errors = e.list.map(_.getMessage)
        throw new AeromockLoadDataException(NonEmptyList(errors.head, errors.drop(1): _*))
      }
    }

    val reducedMap = mergedMap - project._naming.response
    (scanMap(reducedMap)(variableHelper.variableConversion), customResponse)
  }
}

/**
 * Factory of [[ameba.aeromock.server.http.JsonApiResponseWriter]]
 * @author stormcat24
 */
object JsonApiResponseWriterFactory {

  /**
   * Create [[ameba.aeromock.server.http.JsonApiResponseWriter]].
   * @param project [[ameba.aeromock.config.entity.Project]]
   * @param variableHelper [[ameba.aeromock.core.builtin.BuiltinVariableHelper]]
   * @param data Iterable object
   * @return [[ameba.aeromock.server.http.JsonApiResponseWriter]]
   */
  def create(project: Project, variableHelper: BuiltinVariableHelper, data: Iterable[_]): JsonApiResponseWriter = {
    data match {
      case m: Map[Any, Any] @unchecked => new JsonApiMapResponseWriter(project, variableHelper, m)
      case l: Seq[Any] @unchecked => new JsonApiArrayResponseWriter(project, variableHelper, l)
    }
  }

}
