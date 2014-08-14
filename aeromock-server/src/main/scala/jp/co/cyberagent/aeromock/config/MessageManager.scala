package jp.co.cyberagent.aeromock.config

import java.util.ResourceBundle

import jp.co.cyberagent.aeromock.AeromockException

/**
 * Multi language message manager.
 * @author stormcat24
 */
object MessageManager {

  def getMessage(key: Class[_ <: AeromockException], args: AnyRef*): String = getMessageRaw(key.getName, args:_*)
  def getMessage(key: String, args: AnyRef*): String = getMessageRaw(key, args:_*)

  private def getMessageRaw(key: String, args: AnyRef*): String = {
    val bundle = ResourceBundle.getBundle("messages.message")

    Option(bundle.getString(key)) match {
      case None => null
      case Some(template) => args.zipWithIndex.foldLeft(template)((s, arg) => s.replace(s"{${arg._2}}", arg._1.toString))
    }
  }
}
