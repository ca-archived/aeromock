package jp.co.cyberagent.aeromock.protobuf

import com.google.protobuf.{ByteString, CodedOutputStream}

import com.google.protobuf.CodedOutputStream._
import com.squareup.protoparser.MessageType
import jp.co.cyberagent.aeromock.protobuf.ProtoFieldLabel._
import jp.co.cyberagent.aeromock.helper._

import scalaz._
import Scalaz._

import scala.collection.JavaConverters._

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
  def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): ValidationNel[String, A]
  def isMessage: Boolean = false

  def toValue(name: String, value: Option[Any], tag: Int, dependencies: Map[String, List[ProtoField]]): ValidationNel[String, Option[ProtoValue[_, _]]] = {
    (label, value) match {
      case (OPTIONAL, None) => none[ProtoValue[_, _]].successNel[String]
      case (OPTIONAL, Some(v)) => {
        cast(v, dependencies, tag) match {
          case Success(s) => ProtoSingleValue(this, s, tag).some.successNel[String]
          case Failure(errors) => {
            val error =
              s"""> at ${label} ${typeName} ${name} = ${tag} : (Illegal type)
                 |>> actual value is ${v}""".stripMargin
            error.failureNel[Option[ProtoValue[_, _]]]
          }
        }
      }
      case (REPEATED, Some(list: List[Any])) => {
        list.map(cast(_, dependencies, tag) match {
          case Success(s) => s.successNel[String]
          case Failure(errors) => {
            val error =
              s"""> at ${label} ${typeName} ${name} = ${tag} : (element of array is illegal type)
                 |>> actual value is ${list.asJava}""".stripMargin
            error.failureNel[A]
          }
        }).sequenceU match {
          case Success(s) => ProtoListValue(this, s, tag).some.successNel[String]
          case Failure(errors) => errors.failure[Option[ProtoValue[_, _]]]
        }
      }
      case (REPEATED, Some(v)) => {
        val error =
          s"""> at ${label} ${typeName} ${name} = ${tag} : (Not array data)
             |>> actual value is ${v}""".stripMargin
        error.failureNel[Option[ProtoValue[_, _]]]
      }
      case (REPEATED, _) => ProtoListValue(this, List.empty, tag).asInstanceOf[ProtoValue[_, _]].some.successNel[String]
      case (REQUIRED, Some(v)) => {
        cast(v, dependencies, tag) match {
          case Success(s) => ProtoSingleValue(this, s, tag).some.successNel[String]
          case Failure(errors) => {
            val error =
              s"""> at ${label} ${typeName} ${name} = ${tag} : (Illegal type)
                 |>> actual value is ${v}""".stripMargin
            error.failureNel[Option[ProtoValue[_, _]]]
          }
        }
      }
      case (REQUIRED, _) => {
        s"> at ${label} ${typeName} ${name} = ${tag} : (Not specified data)".failureNel[Option[ProtoValue[_, _]]]
      }
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
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): ValidationNel[String, Double] = {
      trye(value.toString.toDouble) match {
        case Right(v) => v.successNel[String]
        case Left(e) => e.getMessage.failureNel[Double]
      }
    }
  }

  case class FLOAT(label: ProtoFieldLabel) extends ProtoFieldType[Float] {
    override val typeName = "float"
    override def computeSize(tag: Int, value: Float): Int = computeFloatSize(tag, value)
    override def computeSizeNoTag(value: Float): Int = computeFloatSizeNoTag(value)
    override def write(output: CodedOutputStream, tag: Int, value: Float): Unit = output.writeFloat(tag, value)
    override def writeNoTag(output: CodedOutputStream, value: Float): Unit = output.writeFloatNoTag(value)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): ValidationNel[String, Float] = {
      trye(value.toString.toFloat) match {
        case Right(v) => v.successNel[String]
        case Left(e) => e.getMessage.failureNel[Float]
      }
    }
  }

  case class INT32(label: ProtoFieldLabel) extends ProtoFieldType[Int] {
    override val typeName = "int32"
    override def computeSize(tag: Int, value: Int): Int = computeInt32Size(tag, value)
    override def computeSizeNoTag(value: Int): Int = computeInt32SizeNoTag(value)
    override def write(output: CodedOutputStream, tag: Int, value: Int): Unit = output.writeInt32(tag, value)
    override def writeNoTag(output: CodedOutputStream, value: Int): Unit = output.writeInt32NoTag(value)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): ValidationNel[String, Int] = {
      trye(value.toString.toInt) match {
        case Right(v) => v.successNel[String]
        case Left(e) => e.getMessage.failureNel[Int]
      }
    }
  }

  case class INT64(label: ProtoFieldLabel) extends ProtoFieldType[Long] {
    override val typeName = "int64"
    override def computeSize(tag: Int, value: Long): Int = computeInt64Size(tag, value)
    override def computeSizeNoTag(value: Long): Int = computeInt64SizeNoTag(value)
    override def write(output: CodedOutputStream, tag: Int, value: Long): Unit = output.writeInt64(tag, value)
    override def writeNoTag(output: CodedOutputStream, value: Long): Unit = output.writeInt64NoTag(value)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): ValidationNel[String, Long] = {
      trye(value.toString.toLong) match {
        case Right(v) => v.successNel[String]
        case Left(e) => e.getMessage.failureNel[Long]
      }
    }
  }

  case class UINT32(label: ProtoFieldLabel) extends ProtoFieldType[Int] {
    override val typeName = "uint32"
    override def computeSize(tag: Int, value: Int): Int = computeUInt32Size(tag, value)
    override def computeSizeNoTag(value: Int): Int = computeUInt32SizeNoTag(value)
    override def write(output: CodedOutputStream, tag: Int, value: Int): Unit = output.writeUInt32(tag, value)
    override def writeNoTag(output: CodedOutputStream, value: Int): Unit = output.writeUInt32NoTag(value)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): ValidationNel[String, Int] = {
      trye(value.toString.toInt) match {
        case Right(v) => v.successNel[String]
        case Left(e) => e.getMessage.failureNel[Int]
      }
    }
  }

  case class UINT64(label: ProtoFieldLabel) extends ProtoFieldType[Long] {
    override val typeName = "uint64"
    override def computeSize(tag: Int, value: Long): Int = computeUInt64Size(tag, value)
    override def computeSizeNoTag(value: Long): Int = computeUInt64SizeNoTag(value)
    override def write(output: CodedOutputStream, tag: Int, value: Long): Unit = output.writeUInt64(tag, value)
    override def writeNoTag(output: CodedOutputStream, value: Long): Unit = output.writeUInt64NoTag(value)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): ValidationNel[String, Long] = {
      trye(value.toString.toLong) match {
        case Right(v) => v.successNel[String]
        case Left(e) => e.getMessage.failureNel[Long]
      }
    }
  }

  case class SINT32(label: ProtoFieldLabel) extends ProtoFieldType[Int] {
    override val typeName = "sint32"
    override def computeSize(tag: Int, value: Int): Int = computeSInt32Size(tag, value)
    override def computeSizeNoTag(value: Int): Int = computeSInt32SizeNoTag(value)
    override def write(output: CodedOutputStream, tag: Int, value: Int): Unit = output.writeSInt32(tag, value)
    override def writeNoTag(output: CodedOutputStream, value: Int): Unit = output.writeSInt32NoTag(value)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): ValidationNel[String, Int] = {
      trye(value.toString.toInt) match {
        case Right(v) => v.successNel[String]
        case Left(e) => e.getMessage.failureNel[Int]
      }
    }
  }

  case class SINT64(label: ProtoFieldLabel) extends ProtoFieldType[Long] {
    override val typeName = "sint64"
    override def computeSize(tag: Int, value: Long): Int = computeSInt64Size(tag, value)
    override def computeSizeNoTag(value: Long): Int = computeSInt64SizeNoTag(value)
    override def write(output: CodedOutputStream, tag: Int, value: Long): Unit = output.writeSInt64(tag, value)
    override def writeNoTag(output: CodedOutputStream, value: Long): Unit = output.writeSInt64NoTag(value)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): ValidationNel[String, Long] = {
      trye(value.toString.toLong) match {
        case Right(v) => v.successNel[String]
        case Left(e) => e.getMessage.failureNel[Long]
      }
    }
  }

  case class FIXED32(label: ProtoFieldLabel) extends ProtoFieldType[Int] {
    override val typeName = "fixed32"
    override def computeSize(tag: Int, value: Int): Int = computeFixed32Size(tag, value)
    override def computeSizeNoTag(value: Int): Int = computeFixed32SizeNoTag(value)
    override def write(output: CodedOutputStream, tag: Int, value: Int): Unit = output.writeFixed32(tag, value)
    override def writeNoTag(output: CodedOutputStream, value: Int): Unit = output.writeFixed32NoTag(value)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): ValidationNel[String, Int] = {
      trye(value.toString.toInt) match {
        case Right(v) => v.successNel[String]
        case Left(e) => e.getMessage.failureNel[Int]
      }
    }
  }

  case class FIXED64(label: ProtoFieldLabel) extends ProtoFieldType[Long] {
    override val typeName = "fixed64"
    override def computeSize(tag: Int, value: Long): Int = computeFixed64Size(tag, value)
    override def computeSizeNoTag(value: Long): Int = computeFixed64SizeNoTag(value)
    override def write(output: CodedOutputStream, tag: Int, value: Long): Unit = output.writeFixed64(tag, value)
    override def writeNoTag(output: CodedOutputStream, value: Long): Unit = output.writeFixed64NoTag(value)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): ValidationNel[String, Long] = {
      trye(value.toString.toLong) match {
        case Right(v) => v.successNel[String]
        case Left(e) => e.getMessage.failureNel[Long]
      }
    }
  }

  case class SFIXED32(label: ProtoFieldLabel) extends ProtoFieldType[Int] {
    override val typeName = "sfixed32"
    override def computeSize(tag: Int, value: Int): Int = computeSFixed32Size(tag, value)
    override def computeSizeNoTag(value: Int): Int = computeSFixed32SizeNoTag(value)
    override def write(output: CodedOutputStream, tag: Int, value: Int): Unit = output.writeSFixed32(tag, value)
    override def writeNoTag(output: CodedOutputStream, value: Int): Unit = output.writeSFixed32NoTag(value)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): ValidationNel[String, Int] = {
      trye(value.toString.toInt) match {
        case Right(v) => v.successNel[String]
        case Left(e) => e.getMessage.failureNel[Int]
      }
    }
  }

  case class SFIXED64(label: ProtoFieldLabel) extends ProtoFieldType[Long] {
    override val typeName = "sfixed64"
    override def computeSize(tag: Int, value: Long): Int = computeSFixed64Size(tag, value)
    override def computeSizeNoTag(value: Long): Int = computeSFixed64SizeNoTag(value)
    override def write(output: CodedOutputStream, tag: Int, value: Long): Unit = output.writeSFixed64(tag, value)
    override def writeNoTag(output: CodedOutputStream, value: Long): Unit = output.writeSFixed64NoTag(value)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): ValidationNel[String, Long] = {
      trye(value.toString.toLong) match {
        case Right(v) => v.successNel[String]
        case Left(e) => e.getMessage.failureNel[Long]
      }
    }
  }

  case class BOOL(label: ProtoFieldLabel) extends ProtoFieldType[Boolean] {
    override val typeName = "bool"
    override def computeSize(tag: Int, value: Boolean): Int = computeBoolSize(tag, value)
    override def computeSizeNoTag(value: Boolean): Int = computeBoolSizeNoTag(value)
    override def write(output: CodedOutputStream, tag: Int, value: Boolean): Unit = output.writeBool(tag, value)
    override def writeNoTag(output: CodedOutputStream, value: Boolean): Unit = output.writeBoolNoTag(value)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): ValidationNel[String, Boolean] = {
      trye(value.toString.toBoolean) match {
        case Right(v) => v.successNel[String]
        case Left(e) => e.getMessage.failureNel[Boolean]
      }
    }
  }

  case class STRING(label: ProtoFieldLabel) extends ProtoFieldType[String] {
    override val typeName = "string"
    override def computeSize(tag: Int, value: String): Int = computeBytesSize(tag, getByteString(value))
    override def computeSizeNoTag(value: String): Int = computeBytesSizeNoTag(getByteString(value))
    override def write(output: CodedOutputStream, tag: Int, value: String): Unit = output.writeBytes(tag, getByteString(value))
    override def writeNoTag(output: CodedOutputStream, value: String): Unit = output.writeBytesNoTag(getByteString(value))
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): ValidationNel[String, String] = value.toString.successNel[String]
  }

  case class BYTES(label: ProtoFieldLabel) extends ProtoFieldType[ByteString] {
    override val typeName = "bytes"
    override def computeSize(tag: Int, value: ByteString): Int = computeBytesSize(tag, value)
    override def computeSizeNoTag(value: ByteString): Int = computeBytesSizeNoTag(value)
    override def write(output: CodedOutputStream, tag: Int, value: ByteString): Unit = output.writeBytes(tag, value)
    override def writeNoTag(output: CodedOutputStream, value: ByteString): Unit = output.writeBytesNoTag(value)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): ValidationNel[String, ByteString] = {
      getByteString(value.toString).successNel[String]
    }
  }

  case class ENUM(typeName: String, values: Map[String, Int], label: ProtoFieldLabel) extends ProtoFieldType[Int] {
    override def computeSize(tag: Int, value: Int): Int = computeEnumSize(tag, value)
    override def computeSizeNoTag(value: Int): Int = computeEnumSizeNoTag(value)
    override def write(output: CodedOutputStream, tag: Int, value: Int): Unit = output.writeEnum(tag, value)
    override def writeNoTag(output: CodedOutputStream, value: Int): Unit = output.writeEnumNoTag(value)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): ValidationNel[String, Int] = values.get(value.toString) match {
      case Some(v) => v.successNel[String]
      case None => s"'${value.toString}' is not defined enum value!!".failureNel[Int]
    }
  }

  case class MESSAGE(typeName: String, label: ProtoFieldLabel, nests: List[ProtoField] = List.empty) extends ProtoFieldType[ProtoMessageValue[_]] {
    override def computeSize(tag: Int, value: ProtoMessageValue[_]): Int = {
      computeTagSize(tag) + computeRawVarint32Size(value.serializedSize) + value.serializedSize
    }
    override def writeNoTag(output: CodedOutputStream, value: ProtoMessageValue[_]): Unit = value.write(output)
    override def computeSizeNoTag(value: ProtoMessageValue[_]): Int = value.serializedSize
    override def write(output: CodedOutputStream, tag: Int, value: ProtoMessageValue[_]): Unit = value.write(output)
    override def cast(value: Any, dependencies: Map[String, List[ProtoField]], tag: Int): ValidationNel[String, ProtoMessageValue[_]] = {
      dependencies.get(typeName) match {
        case Some(dep) => {
          for {
            value <- {
              dep.map(_.toValue(value.asInstanceOf[Map[Any, Any]], dependencies)).sequenceU
            }
          } yield (ProtoMessageValue(MESSAGE(typeName, label), value.flatten, tag))
        }
        case None => {
          for {
            value <- {
              nests.map(_.toValue(value.asInstanceOf[Map[Any, Any]], dependencies)).sequenceU
            }
          } yield (ProtoMessageValue(MESSAGE(typeName, label), value.flatten, tag))
        }
      }
    }

    override def isMessage: Boolean = true
  }

  def valueOf(typeName: String, label: ProtoFieldLabel): ProtoFieldType[_] = {
    typeName match {
      case "double" => DOUBLE(label)
      case "float" => FLOAT(label)
      case "int32" => INT32(label)
      case "int64" => INT64(label)
      case "uint32" => UINT32(label)
      case "uint64" => UINT64(label)
      case "sint32" => SINT32(label)
      case "sint64" => SINT64(label)
      case "fixed32" => FIXED32(label)
      case "fixed64" => FIXED64(label)
      case "sfixed32" => SFIXED32(label)
      case "sfixed64" => SFIXED64(label)
      case "bool" => BOOL(label)
      case "string" => STRING(label)
      case "bytes" => BYTES(label)
      case _ => MESSAGE(typeName, label)
    }
  }

}
