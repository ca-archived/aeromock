package jp.co.cyberagent.aeromock.proxy

import jp.co.cyberagent.aeromock.helper.DeepTraversal
import DeepTraversal._
import jp.co.cyberagent.aeromock.data.InstanceProjection
import javassist.CtClass
import org.slf4j.LoggerFactory

/**
 * Implementation of dynamic hash proxy for Java.
 * @author stormcat24
 */
abstract class DynamicHashProxyJava(parameter: ProxyParameter) extends DynamicProxy with java.util.Map[Any, Any] {

  val $properties = new java.util.HashMap[Any, Any]

  override def size(): Int = $properties.size

  override def isEmpty(): Boolean = $properties.isEmpty

  override def containsKey(key: Any): Boolean = $properties.containsKey(key)

  override def containsValue(value: Any): Boolean = $properties.containsValue(value)

  override def get(key: Any): Any = $properties.get(key)

  override def put(key: Any, value: Any): Any = $properties.put(key, value)

  override def remove(key: Any): Any = $properties.remove(key)

  override def putAll(map: java.util.Map[_ <: Any, _ <: Any]): Unit = $properties.putAll(map)

  override def clear(): Unit = $properties.clear

  override def keySet(): java.util.Set[Any] = $properties.keySet

  override def values(): java.util.Collection[Any] = $properties.values()

  override def entrySet(): java.util.Set[java.util.Map.Entry[Any, Any]] = $properties.entrySet

  override def toString(): String = {
    if (parameter.jsonObject) {
      import org.json4s._
      import org.json4s.native.Serialization
      import org.json4s.native.Serialization.write
      implicit val formats = Serialization.formats(NoTypeHints)

      write(asScalaMap($properties){a => a})
    } else {
      $properties.toString()
    }
  }

}

class DynamicHashProxyJavaBuilder(projection: InstanceProjection)
  extends DynamicDataProxyBuilder[DynamicHashProxyJava](projection) {

  val LOG = LoggerFactory.getLogger(this.getClass())

  override def decorateCtClass(ctClass: CtClass) {

    projection.methods.foreach { methodDef =>
      // java.util.Mapのメソッド名と被る場合は追加させない
      if (!javaMapMethods.contains(methodDef.name)) {
        val method = createMethod(ctClass, methodDef)
        LOG.debug("##DynamicProxy## [method]= {}", methodDef)
        ctClass.addMethod(method)
      } else {
        LOG.debug("##DynamicProxy## [warning] {} is already defined in java.util.Map", methodDef.name)
      }
    }
  }

  override def decorateInstance(instance: DynamicHashProxyJava) {

    projection.properties.foreach { property =>
      instance.put(property.key.key, projection.decoration(projection.processPropertyJava(property.value)))
    }
  }
}
