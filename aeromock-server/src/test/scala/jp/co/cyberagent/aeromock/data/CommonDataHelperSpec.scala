package jp.co.cyberagent.aeromock.data

import java.nio.file.Paths

import jp.co.cyberagent.aeromock.core.http.VariableManager
import jp.co.cyberagent.aeromock.{AeromockScriptBadReturnTypeException, AeromockScriptExecutionException, SpecSupport}
import jp.co.cyberagent.aeromock.config.Naming
import org.specs2.mutable.{Tables, Specification}

/**
 *
 * @author stormcat24
 */
class CommonDataHelperSpec extends Specification with Tables with SpecSupport {

  val root = getResourcePath("data/CommonDataHelper/ajax/")

  "getMergedDataMap" should {

    "empty" in {
      val script = Paths.get("not_exists.groovy")

      val helper = new CommonDataHelper(Naming())
      helper.getMergedDataMap(root, script) must beEmpty
    }

    "illegal script" in {
      "script"                             | "expect"                                 |
      "data/CommonDataHelper/execution_error.groovy" ! throwA[AeromockScriptExecutionException] |
      "data/CommonDataHelper/illegal_return_value.groovy" ! throwA[AeromockScriptBadReturnTypeException] |> { (script, expect) =>

        val helper = new CommonDataHelper(Naming())
        VariableManager.initializeRequestMap(Map.empty)
        VariableManager.initializeOriginalVariableMap(new java.util.HashMap[String, AnyRef])
        helper.getMergedDataMap(root, getResourcePath(script)) must expect
      }
    }

    "acceptable" in {
      "script" | "expect" |
      "data/CommonDataHelper/empty.groovy" ! Map.empty[Any, Any] |
      "data/CommonDataHelper/use_gstring.groovy" ! Map[Any, Any]("prop1" -> "prop1Value") |
      "data/CommonDataHelper/use_string.groovy"  ! Map[Any, Any]("prop1" -> "prop1Value")  |> { (script, expect) =>

        val helper = new CommonDataHelper(Naming())
        VariableManager.initializeRequestMap(Map.empty)
        VariableManager.initializeOriginalVariableMap(new java.util.HashMap[String, AnyRef])

        helper.getMergedDataMap(root, getResourcePath(script)) must_== expect
      }
    }
  }

  // TODO specs for mergeAdditional
}
