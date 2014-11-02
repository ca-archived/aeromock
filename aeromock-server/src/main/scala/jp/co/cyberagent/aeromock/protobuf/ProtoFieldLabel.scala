package jp.co.cyberagent.aeromock.protobuf

import com.squareup.protoparser.MessageType
import jp.co.cyberagent.aeromock.AeromockSystemException
import scalaz._
import Scalaz._

/**
 *
 * @author stormcat24
 */
sealed trait ProtoFieldLabel {
  val name: String

  override def toString(): String = name
}

object ProtoFieldLabel {

  case object REQUIRED extends ProtoFieldLabel {
    override val name = "required"
  }

  case object OPTIONAL extends ProtoFieldLabel {
    override val name = "optional"
  }

  case object REPEATED extends ProtoFieldLabel {
    override val name = "repeated"
  }

  val map = Map(
    MessageType.Label.REQUIRED -> REQUIRED,
    MessageType.Label.OPTIONAL -> OPTIONAL,
    MessageType.Label.REPEATED -> REPEATED
  )

  def valueOf(label: MessageType.Label): ValidationNel[String, ProtoFieldLabel] = {
    map.get(label) match {
      case Some(v) => v.successNel[String]
      case _ => {
        s"label '${label.toString}' is not supported.".failureNel[ProtoFieldLabel]
      }
    }
  }
}
