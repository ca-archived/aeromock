package jp.co.cyberagent.aeromock.proxy

import jp.co.cyberagent.aeromock.core.el.ELContext
import jp.co.cyberagent.aeromock.core.javassist.DynamicProxyBuilder
import scala.reflect.ClassTag
import javassist.CtClass
import jp.co.cyberagent.aeromock.data.MethodDef
import javassist.CtMethod
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.data.DynamicMethodValueStore
import javassist.Modifier
import jp.co.cyberagent.aeromock.data.InstanceProjection
import scala.collection.JavaConverters._

/**
 * Builder to create dynamic proxy to template.
 * @author stormcat24
 */
abstract class DynamicDataProxyBuilder[C: ClassTag](projection: InstanceProjection) extends DynamicProxyBuilder[C] {

  lazy val javaMapMethods = classOf[java.util.Map[_, _]].getMethods().map(_.getName())
  lazy val FQDN_ELCONTEXT = getObjectFqdn(ELContext)

  protected def createMethod(proxy: CtClass, method: MethodDef): CtMethod = {
    val content = s"""
      public Object ${method.name}(Object[] args) {
          String key = "${method.name}";
          return ${getObjectFqdn(DynamicMethodValueStore)}.fetch(this.getClass().getName(), key);
      }
    """

    method.value match {
      case instance: InstanceProjection => {
        val delegateInstance = instance.toInstanceJava
        val ctMethod = CtMethod.make(content, proxy)
        DynamicMethodValueStore.put(proxy.getName(), method.name, delegateInstance)
        ctMethod.setModifiers(ctMethod.getModifiers() | Modifier.VARARGS)
        ctMethod
      }
      case v => {
        val newContent = projection.processPropertyJava(v) match {
          case s: String => {
            DynamicMethodValueStore.put(proxy.getName(), method.name, projection.variableMap.asJava)
            val evalValue = s"""\"${s.replace("\"", "\\\"")}\""""
            s"""
            public Object ${method.name}(Object[] args) {
                java.util.Map map = new java.util.HashMap();
                map.put("ARGUMENTS", args);
                map.putAll((java.util.Map) ${getObjectFqdn(DynamicMethodValueStore)}.fetch(this.getClass().getName(), "${method.name}"));
                ${FQDN_ELCONTEXT} context = ${FQDN_ELCONTEXT}.create(map);
                return context.eval(${evalValue});
            }"""
          }
          case s => {
            DynamicMethodValueStore.put(proxy.getName(), method.name, s)
            content
          }
        }

        val ctMethod = CtMethod.make(newContent, proxy)
        ctMethod.setModifiers(ctMethod.getModifiers() | Modifier.VARARGS)
        ctMethod
      }
    }
  }

}
