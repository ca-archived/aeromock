package jp.co.cyberagent.aeromock.protobuf

import jp.co.cyberagent.aeromock.helper.DeepTraversal._
import jp.co.cyberagent.aeromock.{AeromockIllegalProtoException, AeromockProtoTypeNotSpecifiedException, AeromockProtoFileNotFoundException}
import jp.co.cyberagent.aeromock.config.Project
import jp.co.cyberagent.aeromock.core.el.VariableHelper
import jp.co.cyberagent.aeromock.core.http.{AeromockHttpRequest, VariableManager}
import jp.co.cyberagent.aeromock.server.http.{ResponseDataSupport, RenderResult}
import jp.co.cyberagent.aeromock.helper._
import org.apache.commons.lang3.StringUtils
import scaldi.{Injectable, Injector}
import scala.collection.JavaConverters._
import scalaz._
import Scalaz._

/**
 *
 * @author stormcat24
 */
object ProtobufResponseService extends AnyRef with Injectable with ResponseDataSupport {

  def render(request: AeromockHttpRequest)(implicit inj: Injector): RenderResult[Array[Byte]] = {
    val project = inject[Project]
    val protobuf = project._protobuf
    val naming = project._naming

    val protoFile = protobuf.apiPrefix match {
      case Some(prefix) => protobuf.root / prefix / request.url + ".proto"
      case None => protobuf.root / request.url + ".proto"
    }
    if (!protoFile.exists) {
      throw new AeromockProtoFileNotFoundException(protoFile.toString)
    }

    val response = createResponseData(project, request)
    val variableHelper = new VariableHelper(VariableManager.getRequestMap ++ VariableManager.getOriginalVariableMap().asScala)

    val apiTypeName = response._1.get(naming.`type`) match {
      case Some(apiTypeName: String) if StringUtils.isNotBlank(apiTypeName) => apiTypeName
      case _ => throw new AeromockProtoTypeNotSpecifiedException(request.url)
    }

    val filteredMap = scanMap(response._1 - naming.`type`)(variableHelper.variableConversion)

    val parser = new ProtoFileParser(protobuf.root)
    parser.parseProto(protoFile) match {
      case Success(parsedProto) => RenderResult(parsedProto.buildData(apiTypeName, filteredMap), response._2, false)
      case Failure(f) => throw new AeromockIllegalProtoException(f.toList.mkString("\n"))
    }
  }
}
