package jp.co.cyberagent.aeromock.helper

import java.nio.file.Paths
import java.util.Locale

import jp.co.cyberagent.aeromock.SpecSupport
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

  Locale.setDefault(Locale.US)
  "StringContextHelper" should {
    "none placeholder message" in {
      message"test.none.placeholder" must_== "None Placeholder"
    }

    "has placeholder message" in {
      "without arguments" in {
        message"test.has.placeholder" must_== "Has Placeholder {0}, {1}."
      }
      "with arguments, sequentially" in {
        message"test.has.placeholder${"arg1"}${"arg2"}" must_== "Has Placeholder arg1, arg2."
      }
      "with arguments, as varargs" in {
        message"test.has.placeholder${Seq("arg1", "arg2"): _*}" must_== "Has Placeholder arg1, arg2."
      }
    }
  }

  "PathHelper" should {
    val dummyDir = Paths.get("test")
    val testRootPath = getResourcePath("pathtest")

    "exists" in {
      "input"                       | "expect" |
      testRootPath                  ! true |
      testRootPath / "dummy.txt"    ! true |
      testRootPath / "noexists.txt" ! false |> { (input, expect) =>
        input.exists must_== expect
      }

    }

    "isDirectory" in {
      "input"                         | "expect" |
        testRootPath                  ! true |
        testRootPath / "dummy.txt"    ! false |
        testRootPath / "noexists.txt" ! false |> { (input, expect) =>
        input.isDirectory must_== expect
      }
    }

    "withoutExtension" in {
      "input"                     | "expect" |
        dummyDir                  ! dummyDir |
        dummyDir / "dummy.txt"    ! dummyDir / "dummy" |
        dummyDir / "noexists.txt" ! dummyDir / "noexists" |> { (input, expect) =>
        input.withoutExtension must_== expect
      }
    }

    "getChildren" in {
      val targetDir = testRootPath / "getChildren"
      targetDir.getChildren() must_== Seq("file1.txt", "file2.txt").map(targetDir / _)
    }

    "+" in {
      "input"     | "expect" |
      ""          ! Paths.get("test") |
      "child"     ! Paths.get("testchild") |
      "/child"    ! Paths.get("test/child") |> { (input, expect) =>
        dummyDir + input must_== expect
      }
    }

    "/" in {
      "illegal values" in {
        dummyDir / "" must throwA[IllegalArgumentException]
        dummyDir / " " must throwA[IllegalArgumentException]
      }

      "input" in {
        "input" | "expect" |
        "child" ! Paths.get("test/child") |
        "child/child" ! Paths.get("test/child/child") |
        "/child" ! Paths.get("test/child") |
        "../parent" ! Paths.get("parent") |> { (input, expect) =>
          dummyDir / input must_== expect
        }
      }
    }

    "toCheckSum" in {

      val path = getResourcePath("checksum-test.txt")
      path.toCheckSum must_== "d8e8fca2dc0f896fd7cb4cb0031ba249"
    }
  }

}

object DummyObject
