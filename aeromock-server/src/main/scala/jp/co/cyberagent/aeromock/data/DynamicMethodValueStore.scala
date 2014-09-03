package jp.co.cyberagent.aeromock.data

import org.apache.commons.lang3.StringUtils

object DynamicMethodValueStore {

  type JMap = java.util.Map[String, java.util.Map[String, Any]]

  val threadLocal = new ThreadLocal[JMap] {

    override def initialValue(): JMap = new java.util.HashMap[String, java.util.Map[String, Any]]

    override def get(): JMap = super.get

    override def set(map: JMap): Unit = super.set(map)

    override def remove(): Unit = super.remove
  }

  def initialize(): Unit = {
    threadLocal.remove()
    threadLocal.set(new java.util.HashMap[String, java.util.Map[String, Any]])
  }

  def put(fqdn: String, methodName: String, value: Any) {
    require(StringUtils.isNotBlank(fqdn))
    require(StringUtils.isNotBlank(methodName))

    if (value != null) {
      val methodMap = Option(threadLocal.get().get(fqdn)) match {
        case None => {
          val map = new java.util.HashMap[String, Any]()
          threadLocal.get().put(fqdn, map)
          map
        }
        case Some(map) => map
      }
      methodMap.put(methodName, value)
    }
  }

  /**
   * Fetch volatile object on thread scope.
   * @param fqdn FQDN
   * @param methodName method name
   * @return volatile object
   */
  def fetch(fqdn: String, methodName: String): Any = {
    require(StringUtils.isNotBlank(fqdn))
    require(StringUtils.isNotBlank(methodName))

    // [note] Return type is not Option, because easier to use at Java.
    Option(threadLocal.get().get(fqdn)) match {
      case None => null
      case Some(methodMap) => methodMap.get(methodName)
    }
  }
}
