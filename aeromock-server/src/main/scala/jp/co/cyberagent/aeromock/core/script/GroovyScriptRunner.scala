package jp.co.cyberagent.aeromock.core.script

import java.nio.file.Path

import groovy.lang._
import jp.co.cyberagent.aeromock.core.{CacheKey, ObjectCache}
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.{AeromockScriptBadReturnTypeException, AeromockScriptExecutionException}
import org.codehaus.groovy.runtime.InvokerHelper

import scala.io.Source
import scala.reflect.ClassTag

/**
 * Simple groovy script runner.
 * <p>Consider return type.</p>
 * @param script path of script
 * @author stromcat24
 */
class GroovyScriptRunner[ReturnType: ClassTag](script: Path) {

  val returnClass = implicitly[ClassTag[ReturnType]].runtimeClass

  /**
   * execute script.
   * @param binding [[groovy.lang.Binding]]
   * @param arguments array arguments
   * @return return value by script
   */
  def run(binding: Binding, arguments: Array[String] = Array.empty[String]): ReturnType = {
    require(binding != null)

    val checkSum = script.toCheckSum
    val scriptClass = ObjectCache.get(CacheKey[Class[_]](script.toString, checkSum)) match {
      case None => {
        val content = Source.fromFile(script.toFile, "UTF-8").mkString

        val loader = GroovyClassLoaderPool.loader
        val source = new GroovyCodeSource(content, script.getFileName().toString(), script.getParent.toString)
        val scriptClass = loader.parseClass(source, false)
        ObjectCache.store(CacheKey[Class[_]](script.toString, checkSum), scriptClass)
        scriptClass
      }
      case Some(content) => content
    }

    val executeScript = InvokerHelper.createScript(scriptClass, binding)
    (trye(executeScript.run()) {e => throw new AeromockScriptExecutionException(script, e)} match {
      case Left(e) => throw e
      case Right(v) => trye(returnClass.cast(v).asInstanceOf[ReturnType]) { e =>
        throw new AeromockScriptBadReturnTypeException(returnClass, script, e)
      }
    }).right.get
  }

}

object GroovyClassLoaderPool {
  lazy val loader = new GroovyClassLoader()
}
