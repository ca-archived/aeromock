package jp.co.cyberagent.aeromock.util

object SystemProperty {

  def getValue(key: String): Option[String] = {
    System.getProperty(key) match {
      case null => None
      case value => Some(value)
    }
  }

  def getIntValue(key: String): Option[Int] = {
    System.getProperty(key) match {
      case null => None
      case value => Some(value.toInt)
    }
  }

}
