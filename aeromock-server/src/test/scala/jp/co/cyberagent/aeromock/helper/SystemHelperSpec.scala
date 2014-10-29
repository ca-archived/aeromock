package jp.co.cyberagent.aeromock.helper

import jp.co.cyberagent.aeromock.test.SpecSupport
import org.specs2.mutable.{Tables, Specification}

/**
 *
 * @author stormcat24
 */
class SystemHelperSpec extends Specification with Tables with SpecSupport {

  "SystemHelper" should {

    System.setProperty("INT_PROP_1", "111")
    System.setProperty("INT_PROP_2", "hoge")
    System.setProperty("STRING_PROP_1", "hoge")
    System.setProperty("STRING_PROP_2", "")

    "property#int" in {
      "key"           | "expect"                   |
        "INT_PROP_1"    ! beSome(beRight(111))       |
        "INT_PROP_2"    ! beSome(beLeft[Throwable])  |
        "INT_PROP_NONE" ! beNone                     |> { (key, expect) =>
        SystemHelper.property(key.intStrategy) must expect
      }
    }

    "property#string" in {
      "key"              | "expect"       |
        "STRING_PROP_1"    ! beSome("hoge") |
        "STRING_PROP_NONE" ! beNone         |> { (key, expect) =>
        SystemHelper.property(key) must expect
      }
    }
  }

}
