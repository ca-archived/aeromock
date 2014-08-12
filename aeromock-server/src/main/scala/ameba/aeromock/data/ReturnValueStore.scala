package ameba.aeromock.data

object ReturnValueStore {

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
    
    if (value != null) {
      val methodMap = threadLocal.get().get(fqdn) match {
        case null => {
          val map = new java.util.HashMap[String, Any]()
          threadLocal.get().put(fqdn, map)
          map
        }
        case map => map
      }
      
      methodMap.put(methodName, value)
    }
  }
  
  def fetch(fqdn: String, methodName: String): Any = {
    threadLocal.get().get(fqdn) match {
      case null => null
      case methodMap => methodMap.get(methodName)     
    }
  }
}