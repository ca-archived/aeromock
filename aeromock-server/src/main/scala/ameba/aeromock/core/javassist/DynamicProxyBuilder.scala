package ameba.aeromock.core.javassist

import java.util.UUID
import javassist.{ClassPool, CtClass, LoaderClassPath}

import ameba.aeromock.AeromockSystemException
import ameba.aeromock.helper._

import scala.reflect.ClassTag

/**
 * Builder to create dynamic proxy specified class is parent.
 * @author stormcat24
 */
abstract class DynamicProxyBuilder[C: ClassTag] {

  val baseClass = implicitly[ClassTag[C]].runtimeClass

  val pool = new ClassPool
    pool.appendClassPath(new LoaderClassPath(baseClass.getClassLoader()))

  /**
   * Create dynamic proxy.
   * @param constructArgs Constructor arguments
   * @return instance of proxy
   */
  def build(constructArgs: Array[AnyRef]): C = {

    val ctProxy = createProxyBase
    decorateCtClass(ctProxy)

    val instance = if (constructArgs.isEmpty) {
      ctProxy.toClass().newInstance().asInstanceOf[C]
    } else {
      newInstance(ctProxy.toClass().asInstanceOf[Class[C]], constructArgs)
    }

    decorateInstance(instance)
    instance
  }

  def newInstance[C](clazz: Class[C], constructArgs: Array[AnyRef]): C = {

    val result = clazz.getConstructors().flatMap(c => {
      tryo(c.newInstance(constructArgs: _*).asInstanceOf[C])
    })

    if (result.isEmpty) {
      // TODO Exception
      throw new AeromockSystemException("cannot create instance!")
    }

    result(0)
  }


  private def createProxyBase: CtClass = {
    pool.synchronized {
      val parent = pool.get(baseClass.getName())
      val uuid = UUID.randomUUID().toString().replace("-", "")
      pool.makeClass(s"${baseClass.getName()}$$Proxy${uuid}", parent)
    }
  }

  def decorateCtClass(ctClass: CtClass)

  def decorateInstance(instance: C)

}
