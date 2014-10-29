package jp.co.cyberagent.aeromock.protobuf

import com.google.protobuf.CodedOutputStream
import jp.co.cyberagent.aeromock.AeromockProtoTypeNotFoundException

/**
 *
 * @author stormcat24
 */
case class ParsedProto(
  types: Map[String, List[ProtoField]],
  dependencyTypes: Map[String, List[ProtoField]]
) {

  def buildData(targetTypeName: String, data: Map[Any, Any]): Array[Byte] = {
    val result = types.get(targetTypeName) match {
      case None => throw new AeromockProtoTypeNotFoundException(targetTypeName)
      case Some(value) => value.flatMap(f => f.toValue(data, dependencyTypes))
    }

    val totalSize = result.foldLeft(0)((left, right) => left + right.serializedSize)
    val bytes = new Array[Byte](totalSize)
    val output = CodedOutputStream.newInstance(bytes)
    result.map(_.write(output))
    bytes
  }


}
