package jp.co.cyberagent.aeromock.core.http

object RequestManager {

  // TODO 微妙なのでもうちょっと考える

  val requestMapLocal = new ThreadLocal[Map[String, Any]] {
    override def initialValue(): Map[String, Any] = null

    override def get(): Map[String, Any] = super.get

    override def set(map: Map[String, Any]): Unit = super.set(map)

    override def remove(): Unit = super.remove
  }

  val dataMapLocal = new ThreadLocal[java.util.Map[_, _]] {
    override def initialValue(): java.util.Map[_, _] = null

    override def get(): java.util.Map[_, _] = super.get

    override def set(map: java.util.Map[_, _]): Unit = super.set(map)

    override def remove(): Unit = super.remove
  }

  def initializeRequestMap(map: Map[String, Any]): Unit = {
    requestMapLocal.remove()
    requestMapLocal.set(map)
  }

  def initializeDataMap(map: java.util.Map[_, _]): Unit = {
    dataMapLocal.remove()
    dataMapLocal.set(map)
  }

  def getRequestMap() = requestMapLocal.get()

  def getDataMap() = dataMapLocal.get()

}
