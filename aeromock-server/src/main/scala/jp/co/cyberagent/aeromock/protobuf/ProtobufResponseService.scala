package jp.co.cyberagent.aeromock.protobuf

import io.netty.handler.codec.http.FullHttpRequest
import jp.co.cyberagent.aeromock.helper.DeepTraversal._
import jp.co.cyberagent.aeromock.{AeromockProtoTypeNotSpecifiedException, AeromockProtoFileNotFoundException}
import jp.co.cyberagent.aeromock.config.Project
import jp.co.cyberagent.aeromock.core.el.VariableHelper
import jp.co.cyberagent.aeromock.core.http.VariableManager
import jp.co.cyberagent.aeromock.server.http.{ResponseDataSupport, RenderResult}
import jp.co.cyberagent.aeromock.helper._
import org.apache.commons.lang3.StringUtils
import scaldi.{Injectable, Injector}
import scala.collection.JavaConverters._

/**
 *
 * @author stormcat24
 */
object ProtobufResponseService extends AnyRef with Injectable with ResponseDataSupport {

  def render(request: FullHttpRequest)(implicit inj: Injector): RenderResult[Array[Byte]] = {
    val project = inject[Project]
    val protobuf = project._protobuf
    val naming = project._naming

    val protoFile = protobuf.apiPrefix match {
      case Some(prefix) => protobuf.root / prefix / request.parsedRequest.url + ".proto"
      case None => protobuf.root / request.parsedRequest.url + ".proto"
    }
    if (!protoFile.exists) {
      throw new AeromockProtoFileNotFoundException(protoFile.toString)
    }

    val response = createResponseData(project, request.parsedRequest)
    val variableHelper = new VariableHelper(VariableManager.getRequestMap ++ VariableManager.getOriginalVariableMap().asScala)

    val apiTypeName = response._1.get(naming.`type`) match {
      case Some(apiTypeName: String) if StringUtils.isNotBlank(apiTypeName) => apiTypeName
      case _ => throw new AeromockProtoTypeNotSpecifiedException(request.parsedRequest.url)
    }

    val filteredMap = scanMap(response._1 - naming.`type`)(variableHelper.variableConversion)

    val parser = new ProtoFileParser(protobuf.root)
    val result = parser.parseProto(protoFile)

    RenderResult(result.buildData(apiTypeName, filteredMap), response._2, false)
  }
}
