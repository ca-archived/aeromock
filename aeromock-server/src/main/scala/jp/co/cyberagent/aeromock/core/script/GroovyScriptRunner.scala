package jp.co.cyberagent.aeromock.core.script

import java.nio.file.Path

import groovy.lang.{Binding, GroovyShell, Script}
import jp.co.cyberagent.aeromock.core.{CacheKey, ObjectCache}
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.{AeromockScriptBadReturnTypeException, AeromockScriptExecutionException}

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
    val parsedScript = ObjectCache.get(CacheKey[Script](script.toString, checkSum)) match {
      case None => {
        val content = Source.fromFile(script.toFile, "UTF-8").mkString
        val parsedScript = new GroovyShell().parse(content, script.getFileName().toString())
        ObjectCache.store(CacheKey[Script](script.toString, checkSum), parsedScript)
        parsedScript
      }
      case Some(content) => content
    }

    parsedScript.setBinding(binding)

    (trye(parsedScript.run()) {e => throw new AeromockScriptExecutionException(script, e)} match {
      case Left(e) => throw e
      case Right(v) => trye(returnClass.cast(v).asInstanceOf[ReturnType]) { e =>
        throw new AeromockScriptBadReturnTypeException(returnClass, script, e)
      }
    }).right.get
  }

}
