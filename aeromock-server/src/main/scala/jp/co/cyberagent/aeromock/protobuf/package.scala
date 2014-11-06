package jp.co.cyberagent.aeromock

import java.nio.file.Path

import com.google.protobuf.{WireFormat, ByteString, CodedOutputStream}
import com.google.protobuf.CodedOutputStream._
import com.squareup.protoparser.{ProtoSchemaParser, ProtoFile}
import jp.co.cyberagent.aeromock.protobuf.ProtoFieldTypes.MESSAGE

import scala.language.experimental
import scala.reflect.ClassTag
import jp.co.cyberagent.aeromock.helper._
import scala.language.existentials
import scalaz._
import Scalaz._

/**
 *
 * @author stormcat24
 */
package object protobuf {

  def parseProtoFile(protoFile: Path): ValidationNel[String, ProtoFile] = {
    trye(ProtoSchemaParser.parse(protoFile.toFile)) match {
      case Left(f) => f.getMessage.failureNel[ProtoFile]
      case Right(protoFile) => protoFile.successNel[String]
    }
  }

  def getByteString(value: String): ByteString = ByteString.copyFromUtf8(value)

  def cast[A](value: Any)(implicit tag: ClassTag[A]): Either[Throwable, A] = {
    value match {
      case string: CharSequence => doCast[A](string.toString)
      case i: Number => doCast[A](i.toString)
    }
  }

  val TypeString = classOf[String]
  val TypeInt = classOf[Int]
  val TypeShort = classOf[Short]
  val TypeLong = classOf[Long]
  val TypeFloat = classOf[Float]
  val TypeDouble = classOf[Double]
  val TypeBoolean = classOf[Boolean]

  private[protobuf] def doCast[A](value: String)(implicit tag: ClassTag[A]): Either[Throwable, A] = {
    trye((implicitly[ClassTag[A]].runtimeClass match {
      case TypeString => value.toString
      case TypeInt => value.toInt
      case TypeShort => value.toShort
      case TypeLong => value.toLong
      case TypeFloat => value.toFloat
      case TypeDouble => value.toDouble
      case TypeBoolean => value.toBoolean
    }).asInstanceOf[A])
  }

  case class ProtoField(
    `type`: ProtoFieldType[_],
    name: String,
    tag: Int
  ) {

    def toValue(data: Any, dependencies: Map[String, List[ProtoField]]): ValidationNel[String, Option[ProtoValue[_, _]]] = {
      Option(data).map {
        case dataMap: Map[Any, Any] @unchecked => `type`.toValue(name, dataMap.get(name), tag, dependencies)
      }.getOrElse("${name} of element may be null.".failureNel[Option[ProtoValue[_, _]]])
    }
  }

  trait ProtoValue[A, +B] {
    val field: ProtoFieldType[A]
    val value: B
    val tag: Int
    val serializedSize: Int
    def write(output: CodedOutputStream): Unit
  }

  case class ProtoSingleValue[A] (
    field: ProtoFieldType[A],
    value: A,
    tag: Int
  ) extends ProtoValue[A, A] {
    override lazy val serializedSize: Int = field.computeSize(tag, value)
    override def write(output: CodedOutputStream): Unit = field.write(output, tag, value)
  }

  case class ProtoListValue[A] (
    field: ProtoFieldType[A],
    value: List[A],
    tag: Int
  ) extends ProtoValue[A, List[A]] {
    override lazy val serializedSize: Int = {
      if (field.isMessage) {
        value.asInstanceOf[List[ProtoMessageValue[A]]].foldLeft(0)((left, right) => {
          val size = right.serializedSize
          left + computeTagSize(right.tag) + computeRawVarint32Size(size) + size
        })
      } else {
        value.foldLeft(0)((left, right) => {
          left + field.computeSizeNoTag(right)
        }) + (value.size * computeTagSize(tag))
      }
    }
    override def write(output: CodedOutputStream): Unit = value.map(field.write(output, tag, _))
  }

  case class ProtoMessageValue[A] (
    field: ProtoFieldType[A],
    value: List[ProtoValue[_, _]],
    tag: Int
  ) extends ProtoValue[A, List[ProtoValue[_, _]]] {
    override lazy val serializedSize: Int = {
      value.foldLeft(0)((left, right) => {
        left + (right match {
          case f @ ProtoMessageValue(_, _, ttag) => {
            computeTagSize(ttag) + computeRawVarint32Size(f.serializedSize) + f.serializedSize
          }
          case f => f.serializedSize
        })
      })
    }

    override def write(output: CodedOutputStream): Unit = {
      output.writeTag(tag, WireFormat.WIRETYPE_LENGTH_DELIMITED)
      output.writeRawVarint32(serializedSize)
      value.map(_.write(output))
    }
  }

}


