package jp.co.cyberagent.aeromock

import com.google.protobuf.{WireFormat, ByteString, CodedOutputStream}
import com.google.protobuf.CodedOutputStream._

import scala.language.experimental
import scala.reflect.ClassTag
import jp.co.cyberagent.aeromock.helper._
import scala.language.existentials

/**
 *
 * @author stormcat24
 */
package object protobuf {

  def getByteString(value: String): ByteString = ByteString.copyFromUtf8(value)

  def cast[A](value: Any)(implicit tag: ClassTag[A]): Either[Throwable, A] = {
    value match {
      case string: CharSequence => doCast[A](string.toString)
      case i: Number => doCast[A](i.toString)
    }
  }

  val TypeString = classOf[String]
  val TypeInt = classOf[Int]
  val TypeLong = classOf[Long]
  val TypeFloat = classOf[Float]
  val TypeDouble = classOf[Double]

  private[protobuf] def doCast[A](value: String)(implicit tag: ClassTag[A]): Either[Throwable, A] = {
    trye((implicitly[ClassTag[A]].runtimeClass match {
      case TypeString => value.toString
      case TypeInt => value.toInt
      case TypeLong => value.toLong
      case TypeFloat => value.toFloat
      case TypeDouble => value.toDouble
    }).asInstanceOf[A])
  }

  case class ProtoField(
    `type`: ProtoFieldType[_],
    name: String,
    tag: Int
  ) {

    def toValue(data: Any, dependencies: Map[String, List[ProtoField]]): Option[ProtoProxyValue[_, _]] = {
      val dataMap = data.asInstanceOf[Map[Any, Any]]
      `type`.toValue(dataMap.get(name), tag, dependencies)
    }
  }

  trait ProtoProxyValue[A, B] {
    val field: ProtoFieldType[A]
    val value: B
    val tag: Int
    val serializedSize: Int
    def write(output: CodedOutputStream): Unit
  }

  case class ProtoProxySingleValue[A] (
    field: ProtoFieldType[A],
    value: A,
    tag: Int
  ) extends ProtoProxyValue[A, A] {
    override lazy val serializedSize: Int = field.computeSize(tag, value)
    override def write(output: CodedOutputStream): Unit = field.write(output, tag, value)
  }

  case class ProtoProxyListValue[A] (
    field: ProtoFieldType[A],
    value: List[A],
    tag: Int
  ) extends ProtoProxyValue[A, List[A]] {
    override lazy val serializedSize: Int = {
      value.foldLeft(0)((left, right) => {
        left + (right match {
          case v @ ProtoProxyMessageValue(_, _, ttag) => computeTagSize(ttag) + v.serializedSize
          case _ => field.computeSizeNoTag(right)
        })
      }) + value.size
    }
    override def write(output: CodedOutputStream): Unit = value.map(field.write(output, tag, _))
  }

  case class ProtoProxyMessageValue[A] (
    field: ProtoFieldType[A],
    value: List[ProtoProxyValue[_, _]],
    tag: Int
  ) extends ProtoProxyValue[A, List[ProtoProxyValue[_, _]]] {
    override lazy val serializedSize: Int = {
      value.foldLeft(0)((left, right) => {
        left + (right match {
          case f @ ProtoProxyMessageValue(_, _, ttag) => {
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


