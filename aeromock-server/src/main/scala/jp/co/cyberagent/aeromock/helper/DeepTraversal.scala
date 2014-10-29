package jp.co.cyberagent.aeromock.helper

import scala.collection.JavaConverters._
import scala.collection.immutable.ListMap
import scala.collection.mutable.ArrayBuffer
import scala.language.implicitConversions

/**
 * Supports traversing deeply for collection.
 * @author stormcat24
 */
object DeepTraversal {

  type JMap[K, V] = java.util.Map[K, V]
  type JCollection[E] = java.util.Collection[E]

  val nop = (a: Any) => a

  def asScalaMap[K, V](input: JMap[K, V])(arround: (Any => Any)): Map[K, V] = {
    val buffer = new ArrayBuffer[(K, V)]
    input.asScala.foreach { entry =>
      val tuple = entry match {
        case (key, value: JMap[_, _]) => {
          (key, asScalaMap(value)(arround).asInstanceOf[V])
        }
        case (key, value: JCollection[_]) => (key, asScalaIterable(value)(arround).asInstanceOf[V])
        case (key, value) => (key -> arround(value).asInstanceOf[V])
      }
      buffer += tuple
    }
    ListMap(buffer.toList: _*)
  }

  def asScalaIterable(value: JCollection[_])(arround: (Any => Any)): Iterable[_] = {
    value.asScala.map {
      case entry: JMap[_, _] => asScalaMap(entry)(arround)
      case entry: JCollection[_] => asScalaIterable(entry)(arround)
      case entry => arround(entry)
    }
  }

  def scanMap[K, V](input: Map[K, V])(arround: (Any => Any)): Map[K, V] = {
    input.map {
      case (key, value: Map[_, _]) => (key -> scanMap(value)(arround).asInstanceOf[V])
      case (key, value: Seq[_]) => (key -> scanSeq(value)(arround).asInstanceOf[V])
      case (key, value) => {
        (key -> arround(value).asInstanceOf[V])
      }
    }
  }

  def scanSeq[V](input: Seq[V])(arround: (Any => Any)): Seq[V] = {
    input.map {
      case map: Map[_, _] => scanMap(map)(arround).asInstanceOf[V]
      case seq: Seq[_] => scanSeq(seq)(arround).asInstanceOf[V]
      case e => arround(e).asInstanceOf[V]
    }
  }

  def asJavaMap[K, V](input: Map[K, V])(arround: (Any => Any)): JMap[K, V] = {
    val map = new java.util.LinkedHashMap[K, V]
    input.map {
      case (key, value: Map[_, _]) => map.put(key, asJavaMap(value)(arround).asInstanceOf[V])
      case (key, value: Seq[_]) => map.put(key, asJavaCollection(value)(arround).asInstanceOf[V])
      case (key, value) => map.put(key, arround(value).asInstanceOf[V])
    }
    map
  }

  def asJavaCollection[V](input: Seq[V])(arround: (Any => Any)): JCollection[V] = {
    input.map {
      case entry: Map[_, _] => asJavaMap(entry)(arround).asInstanceOf[V]
      case entry: Seq[_] => asJavaCollection(entry)(arround).asInstanceOf[V]
      case entry => arround(entry).asInstanceOf[V]
    }.asJava
  }
}

