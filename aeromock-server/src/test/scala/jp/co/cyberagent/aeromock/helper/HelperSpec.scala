package jp.co.cyberagent.aeromock.helper

import jp.co.cyberagent.aeromock.test.SpecSupport
import org.specs2.mutable._

/**
 *
 * @author stormcat24
 */
class HelperSpec extends Specification with Tables with SpecSupport {

  "Various utitilies" should {
    "millsToSeconds" in {
      millsToSeconds(1500, 1225) must_== BigDecimal(0.275)
    }

    "getExtension" in {
      "null" in {
        getExtension(null) must throwA[IllegalArgumentException]
      }
      "input = #input" in {

        "input" | "expect" |
          "" ! None |
          "test" ! None |
          "test.txt" ! Some("txt") |
          "test.txt.scala" ! Some("scala") |> {
          (input, expect) => getExtension(input) must_== expect
        }
      }
    }

    "getObjectFqdn" in {
      "null" in {
        getObjectFqdn(null) must throwA[IllegalArgumentException]
      }
      "input" in {

        "input"     | "expect" |
        "test"      ! "java.lang.String" |
        DummyObject ! "jp.co.cyberagent.aeromock.helper.DummyObject" |> {
          (input, expect) => getObjectFqdn(input) must_== expect
        }
      }
    }
  }

  "colors" in {
    red("HOGE") must_== "\u001b[31mHOGE\u001b[00m"
    green("HOGE") must_== "\u001b[32mHOGE\u001b[00m"
    yellow("HOGE") must_== "\u001b[33mHOGE\u001b[00m"
    blue("HOGE") must_== "\u001b[34mHOGE\u001b[00m"
    purple("HOGE") must_== "\u001b[35mHOGE\u001b[00m"
    lightBlue("HOGE") must_== "\u001b[36mHOGE\u001b[00m"
    white("HOGE") must_== "\u001b[37mHOGE\u001b[00m"
  }

}

object DummyObject
