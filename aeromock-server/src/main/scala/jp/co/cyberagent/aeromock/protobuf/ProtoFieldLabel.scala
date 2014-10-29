package jp.co.cyberagent.aeromock.protobuf

import com.squareup.protoparser.MessageType
import jp.co.cyberagent.aeromock.AeromockSystemException

/**
 *
 * @author stormcat24
 */
sealed abstract class ProtoFieldLabel

object ProtoFieldLabel {

  case object REQUIRED extends ProtoFieldLabel

  case object OPTIONAL extends ProtoFieldLabel

  case object REPEATED extends ProtoFieldLabel

  val map = Map(
    MessageType.Label.REQUIRED -> REQUIRED,
    MessageType.Label.OPTIONAL -> OPTIONAL,
    MessageType.Label.REPEATED -> REPEATED
  )

  def valueOf(label: MessageType.Label): ProtoFieldLabel = {
    map.get(label) match {
      case Some(v) => v
      case _ => throw new AeromockSystemException(s"label '${label.toString}' is not supported.")
    }
  }
}
