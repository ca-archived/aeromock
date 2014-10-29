package jp.co.cyberagent.aeromock.protobuf

import com.google.protobuf.{ByteString, CodedOutputStream}

import com.google.protobuf.CodedOutputStream._
import com.squareup.protoparser.MessageType
import jp.co.cyberagent.aeromock.protobuf.ProtoFieldLabel._

/**
 *
 * @author stormcat24
 */
sealed abstract class ProtoFieldType[A] {
  val typeName: String
  val label: ProtoFieldLabel
  def computeSize(tag: Int, value: A): Int
  def computeSizeNoTag(value: A): Int
  def write(output: CodedOutputStream, tag: Int, value: A): Unit
  def writeNoTag(output: CodedOutputStream, value: A): Unit
  def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): A
  def toValue(value: Option[Any], tag: Int, dependencies: Map[String, List[ProtoField]]): Option[ProtoProxyValue[_, _]] = {
    (label, value) match {
      case (OPTIONAL, None) => None
      case (OPTIONAL, Some(v)) => Some(ProtoProxySingleValue(this, cast(v, dependencies, tag), tag))
      case (REPEATED, Some(list: List[Any])) => Some(ProtoProxyListValue(this, list.map(cast(_, dependencies, tag)), tag))
      case (REPEATED, Some(_)) => throw new RuntimeException("TODO list型ではありません")
      case (REPEATED, _) => Some(ProtoProxyListValue(this, List.empty, tag))
      case (REQUIRED, Some(v)) => Some(ProtoProxySingleValue(this, cast(v, dependencies, tag), tag))
      case (REQUIRED, _) => throw new RuntimeException("TODO 値が指定されていません")
    }
  }
}

object ProtoFieldTypes {

  case class DOUBLE(label: ProtoFieldLabel) extends ProtoFieldType[Double] {
    override val typeName = "double"
    override def computeSize(tag: Int, value: Double): Int = computeDoubleSize(tag, value)
    override def computeSizeNoTag(value: Double): Int = computeDoubleSizeNoTag(value)
    override def write(output: CodedOutputStream, tag: Int, value: Double): Unit = output.writeDouble(tag, value)
    override def writeNoTag(output: CodedOutputStream, value: Double): Unit = output.writeDoubleNoTag(value)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): Double = value.toString.toDouble
  }

  case class FLOAT(label: ProtoFieldLabel) extends ProtoFieldType[Float] {
    override val typeName = "float"
    override def computeSize(tag: Int, value: Float): Int = computeFloatSize(tag, value)
    override def computeSizeNoTag(value: Float): Int = computeFloatSizeNoTag(value)
    override def write(output: CodedOutputStream, tag: Int, value: Float): Unit = output.writeFloat(tag, value)
    override def writeNoTag(output: CodedOutputStream, value: Float): Unit = output.writeFloatNoTag(value)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): Float = value.toString.toFloat
  }

  case class INT32(label: ProtoFieldLabel) extends ProtoFieldType[Int] {
    override val typeName = "int32"
    override def computeSize(tag: Int, value: Int): Int = computeInt32Size(tag, value)
    override def computeSizeNoTag(value: Int): Int = computeInt32SizeNoTag(value)
    override def write(output: CodedOutputStream, tag: Int, value: Int): Unit = output.writeInt32(tag, value)
    override def writeNoTag(output: CodedOutputStream, value: Int): Unit = output.writeInt32NoTag(value)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): Int = value.toString.toInt
  }

  case class INT64(label: ProtoFieldLabel) extends ProtoFieldType[Long] {
    override val typeName = "int64"
    override def computeSize(tag: Int, value: Long): Int = computeInt64Size(tag, value)
    override def computeSizeNoTag(value: Long): Int = computeInt64SizeNoTag(value)
    override def write(output: CodedOutputStream, tag: Int, value: Long): Unit = output.writeInt64(tag, value)
    override def writeNoTag(output: CodedOutputStream, value: Long): Unit = output.writeInt64NoTag(value)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): Long = value.toString.toLong
  }

  case class UINT32(label: ProtoFieldLabel) extends ProtoFieldType[Int] {
    override val typeName = "uint32"
    override def computeSize(tag: Int, value: Int): Int = computeUInt32Size(tag, value)
    override def computeSizeNoTag(value: Int): Int = computeUInt32SizeNoTag(value)
    override def write(output: CodedOutputStream, tag: Int, value: Int): Unit = output.writeUInt32(tag, value)
    override def writeNoTag(output: CodedOutputStream, value: Int): Unit = output.writeUInt32NoTag(value)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): Int = value.toString.toInt
  }

  case class UINT64(label: ProtoFieldLabel) extends ProtoFieldType[Long] {
    override val typeName = "uint64"
    override def computeSize(tag: Int, value: Long): Int = computeUInt64Size(tag, value)
    override def computeSizeNoTag(value: Long): Int = computeUInt64SizeNoTag(value)
    override def write(output: CodedOutputStream, tag: Int, value: Long): Unit = output.writeUInt64(tag, value)
    override def writeNoTag(output: CodedOutputStream, value: Long): Unit = output.writeUInt64NoTag(value)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): Long = value.toString.toLong
  }

  case class SINT32(label: ProtoFieldLabel) extends ProtoFieldType[Int] {
    override val typeName = "sint32"
    override def computeSize(tag: Int, value: Int): Int = computeSInt32Size(tag, value)
    override def computeSizeNoTag(value: Int): Int = computeSInt32SizeNoTag(value)
    override def write(output: CodedOutputStream, tag: Int, value: Int): Unit = output.writeSInt32(tag, value)
    override def writeNoTag(output: CodedOutputStream, value: Int): Unit = output.writeSInt32NoTag(value)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): Int = value.toString.toInt
  }

  case class SINT64(label: ProtoFieldLabel) extends ProtoFieldType[Long] {
    override val typeName = "sint64"
    override def computeSize(tag: Int, value: Long): Int = computeSInt64Size(tag, value)
    override def computeSizeNoTag(value: Long): Int = computeSInt64SizeNoTag(value)
    override def write(output: CodedOutputStream, tag: Int, value: Long): Unit = output.writeSInt64(tag, value)
    override def writeNoTag(output: CodedOutputStream, value: Long): Unit = output.writeSInt64NoTag(value)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): Long = value.toString.toLong
  }

  case class FIXED32(label: ProtoFieldLabel) extends ProtoFieldType[Int] {
    override val typeName = "fixed32"
    override def computeSize(tag: Int, value: Int): Int = computeFixed32Size(tag, value)
    override def computeSizeNoTag(value: Int): Int = computeFixed32SizeNoTag(value)
    override def write(output: CodedOutputStream, tag: Int, value: Int): Unit = output.writeFixed32(tag, value)
    override def writeNoTag(output: CodedOutputStream, value: Int): Unit = output.writeFixed32NoTag(value)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): Int = value.toString.toInt
  }

  case class FIXED64(label: ProtoFieldLabel) extends ProtoFieldType[Long] {
    override val typeName = "fixed64"
    override def computeSize(tag: Int, value: Long): Int = computeFixed64Size(tag, value)
    override def computeSizeNoTag(value: Long): Int = computeFixed64SizeNoTag(value)
    override def write(output: CodedOutputStream, tag: Int, value: Long): Unit = output.writeFixed64(tag, value)
    override def writeNoTag(output: CodedOutputStream, value: Long): Unit = output.writeFixed64NoTag(value)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): Long = value.toString.toLong
  }

  case class SFIXED32(label: ProtoFieldLabel) extends ProtoFieldType[Int] {
    override val typeName = "sfixed32"
    override def computeSize(tag: Int, value: Int): Int = computeSFixed32Size(tag, value)
    override def computeSizeNoTag(value: Int): Int = computeSFixed32SizeNoTag(value)
    override def write(output: CodedOutputStream, tag: Int, value: Int): Unit = output.writeSFixed32(tag, value)
    override def writeNoTag(output: CodedOutputStream, value: Int): Unit = output.writeSFixed32NoTag(value)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): Int = value.toString.toInt
  }

  case class SFIXED64(label: ProtoFieldLabel) extends ProtoFieldType[Long] {
    override val typeName = "sfixed64"
    override def computeSize(tag: Int, value: Long): Int = computeSFixed64Size(tag, value)
    override def computeSizeNoTag(value: Long): Int = computeSFixed64SizeNoTag(value)
    override def write(output: CodedOutputStream, tag: Int, value: Long): Unit = output.writeSFixed64(tag, value)
    override def writeNoTag(output: CodedOutputStream, value: Long): Unit = output.writeSFixed64NoTag(value)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): Long = value.toString.toLong
  }

  case class BOOL(label: ProtoFieldLabel) extends ProtoFieldType[Boolean] {
    override val typeName = "bool"
    override def computeSize(tag: Int, value: Boolean): Int = computeBoolSize(tag, value)
    override def computeSizeNoTag(value: Boolean): Int = computeBoolSizeNoTag(value)
    override def write(output: CodedOutputStream, tag: Int, value: Boolean): Unit = output.writeBool(tag, value)
    override def writeNoTag(output: CodedOutputStream, value: Boolean): Unit = output.writeBoolNoTag(value)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): Boolean = value.toString.toBoolean
  }

  case class STRING(label: ProtoFieldLabel) extends ProtoFieldType[String] {
    override val typeName = "string"
    override def computeSize(tag: Int, value: String): Int = computeBytesSize(tag, getByteString(value))
    override def computeSizeNoTag(value: String): Int = computeBytesSizeNoTag(getByteString(value))
    override def write(output: CodedOutputStream, tag: Int, value: String): Unit = output.writeBytes(tag, getByteString(value))
    override def writeNoTag(output: CodedOutputStream, value: String): Unit = output.writeBytesNoTag(getByteString(value))
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): String = value.toString
  }

  case class BYTES(label: ProtoFieldLabel) extends ProtoFieldType[ByteString] {
    override val typeName = "bytes"
    override def computeSize(tag: Int, value: ByteString): Int = computeBytesSize(tag, value)
    override def computeSizeNoTag(value: ByteString): Int = computeBytesSizeNoTag(value)
    override def write(output: CodedOutputStream, tag: Int, value: ByteString): Unit = output.writeBytes(tag, value)
    override def writeNoTag(output: CodedOutputStream, value: ByteString): Unit = output.writeBytesNoTag(value)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): ByteString = getByteString(value.toString)
  }

  case class ENUM(typeName: String, values: Map[String, Int], label: ProtoFieldLabel) extends ProtoFieldType[Int] {
    override def computeSize(tag: Int, value: Int): Int = computeEnumSize(tag, value)
    override def computeSizeNoTag(value: Int): Int = computeEnumSizeNoTag(value)
    override def write(output: CodedOutputStream, tag: Int, value: Int): Unit = output.writeEnum(tag, value)
    override def writeNoTag(output: CodedOutputStream, value: Int): Unit = output.writeEnumNoTag(value)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): Int = values.get(value.toString) match {
      case Some(v) => v
      case None => throw new RuntimeException("not defined enum value!!") // TODO
    }
  }

  case class MESSAGE(typeName: String, label: ProtoFieldLabel) extends ProtoFieldType[ProtoProxyMessageValue[_]] {
    override def computeSize(tag: Int, value: ProtoProxyMessageValue[_]): Int = {
      computeTagSize(tag) + computeRawVarint32Size(value.serializedSize) + value.serializedSize
    }
    override def writeNoTag(output: CodedOutputStream, value: ProtoProxyMessageValue[_]): Unit = value.write(output)
    override def computeSizeNoTag(value: ProtoProxyMessageValue[_]): Int = value.serializedSize
    override def write(output: CodedOutputStream, tag: Int, value: ProtoProxyMessageValue[_]): Unit = value.write(output)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): ProtoProxyMessageValue[_] = {
      dependencies.get(typeName) match {
        case Some(dep) => {
          val list = dep.flatMap(_.toValue(value.asInstanceOf[Map[Any, Any]], dependencies))
          ProtoProxyMessageValue(MESSAGE(typeName, label), list, tag)
        }
        case None => throw new RuntimeException(s"cannot find '${typeName}'.")
      }
    }
  }

  def valueOf(typeName: String, label: MessageType.Label): ProtoFieldType[_] = {
    val convertedLabel = ProtoFieldLabel.valueOf(label)
    typeName match {
      case "double" => DOUBLE(convertedLabel)
      case "float" => FLOAT(convertedLabel)
      case "int32" => INT32(convertedLabel)
      case "int64" => INT64(convertedLabel)
      case "uint32" => UINT32(convertedLabel)
      case "uint64" => UINT64(convertedLabel)
      case "sint32" => SINT32(convertedLabel)
      case "sint64" => SINT64(convertedLabel)
      case "fixed32" => FIXED32(convertedLabel)
      case "fixed64" => FIXED64(convertedLabel)
      case "sfixed32" => SFIXED32(convertedLabel)
      case "sfixed64" => SFIXED64(convertedLabel)
      case "bool" => BOOL(convertedLabel)
      case "string" => STRING(convertedLabel)
      case "bytes" => BYTES(convertedLabel)
      case _ => MESSAGE(typeName, convertedLabel)
    }
  }

}
