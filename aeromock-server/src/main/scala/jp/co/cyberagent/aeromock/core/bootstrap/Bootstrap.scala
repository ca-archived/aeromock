package jp.co.cyberagent.aeromock.core.bootstrap

import javassist.{LoaderClassPath, ClassPool}

/**
 * Bootstrap trait
 */
trait Bootstrap {

  val pool = new ClassPool()
  pool.appendClassPath(new LoaderClassPath(this.getClass().getClassLoader()))

  /**
   * run bootstrap process
   */
  def process(): Unit

}
