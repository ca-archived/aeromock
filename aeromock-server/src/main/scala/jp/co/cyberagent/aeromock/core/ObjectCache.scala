package jp.co.cyberagent.aeromock.core

import scala.reflect.ClassTag

/**
 *
 * @author stormcat24
 */
object ObjectCache {

  val cache = new java.util.concurrent.ConcurrentHashMap[CacheKey[_], AnyRef]()

  def store(key: CacheKey[_], value: AnyRef) {
    cache.put(key, value)
  }

  def get[ValueType](key: CacheKey[ValueType])(implicit tag: ClassTag[ValueType]): Option[ValueType] = {
    Option(cache.get(key)).map(_.asInstanceOf[ValueType])
  }

}

case class CacheKey[ValueType](target: String, checkSum: String)
