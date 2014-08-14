package jp.co.cyberagent.aeromock.proxy

import jp.co.cyberagent.aeromock.data.InstanceProjection
import jp.co.cyberagent.aeromock.helper._
import org.slf4j.LoggerFactory
import javassist.CtClass
import jp.co.cyberagent.aeromock.data.ReturnValueStore
import javassist.CtMethod


/**
 * Base dynamic proxy of List
 * @author stormcat24
 *
 */
abstract class DynamicCollectionProxyJava(parameter: ProxyParameter) extends java.util.ArrayList[Any] with DynamicProxy {

  override def toString(): String = {
    if (parameter.jsonObject) {
      import org.json4s._
      import org.json4s.native.Serialization
      import org.json4s.native.Serialization.write
      import scala.collection.JavaConverters._
      implicit val formats = Serialization.formats(NoTypeHints)
      write(this.asScala)
    } else {
      super.toString()
    }
  }
}

class DynamicCollectionProxyBuilder(projection: InstanceProjection)
  extends DynamicDataProxyBuilder[DynamicCollectionProxyJava](projection) {

  val LOG = LoggerFactory.getLogger(this.getClass())

  override def decorateCtClass(ctClass: CtClass) {

    // add getter method
    projection.properties.map(property => {
      val value = projection.decoration(projection.processPropertyJava(property.value))
      val s = property.key.key.toString()

      val methodName = (if (value.isInstanceOf[Boolean]) "is" else "get") + s.substring(0, 1).toUpperCase() + s.substring(1, s.length())
      val accessor = if (value.isInstanceOf[Boolean]) {
        s"""
          public boolean $methodName() {
              return ${value};
          }
        """
      } else {
        s"""
          public Object $methodName() {
              return ${getObjectFqdn(ReturnValueStore)}.fetch(this.getClass().getName(), "$methodName");
          }
        """
      }

      ReturnValueStore.put(ctClass.getName(), methodName, value)
      CtMethod.make(accessor, ctClass)
    }).foreach(ctClass.addMethod(_))

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

  override def decorateInstance(instance: DynamicCollectionProxyJava) {
    projection.externalList.get.value.foreach(e => {
      instance.add(projection.decoration(projection.processPropertyJava(e)).asInstanceOf[Any])
    })
  }

}
