package jp.co.cyberagent.aeromock.core.script

import groovy.lang.Binding
import jp.co.cyberagent.aeromock.{AeromockScriptBadReturnTypeException, AeromockScriptExecutionException, SpecSupport}
import org.specs2.mutable.{Specification, Tables}

/**
 *
 * @author stormcat24
 */
class GroovyScriptRunnerSpec extends Specification with Tables with SpecSupport {

  "run" should {
    "bad return type" in {
      new GroovyScriptRunner[Unit](getResourcePath("script/script1.groovy"))
        .run(new Binding) must throwA[AeromockScriptBadReturnTypeException]
      new GroovyScriptRunner[Int](getResourcePath("script/script1.groovy"))
        .run(new Binding) must throwA[AeromockScriptBadReturnTypeException]
    }
    "script error" in {
      new GroovyScriptRunner[Unit](getResourcePath("script/execute_error.groovy"))
        .run(new Binding) must throwA[AeromockScriptExecutionException]
    }
    "check return value" in {
      val runner = new GroovyScriptRunner[String](getResourcePath("script/script1.groovy"))
      runner.run(new Binding) must_== "script1!!"
    }
    "use binding" in {
      val binding = new Binding
      binding.setProperty("PARAM1", "VALUE1")
      binding.setProperty("PARAM2", "VALUE2")

      val runner = new GroovyScriptRunner[String](getResourcePath("script/binding.groovy"))
      runner.run(binding) must_== "VALUE1_VALUE2"
    }
    "use cache" in {
      val runner = new GroovyScriptRunner[String](getResourcePath("script/use_cache.groovy"))
      runner.run(new Binding) must_== "use cache"
      runner.run(new Binding) must_== "use cache"
    }
  }
}
