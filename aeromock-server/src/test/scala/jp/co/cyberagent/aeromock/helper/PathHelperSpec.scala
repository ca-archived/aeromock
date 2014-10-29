package jp.co.cyberagent.aeromock.helper

import java.nio.file.Paths

import jp.co.cyberagent.aeromock.test.SpecSupport
import org.specs2.mutable.{Tables, Specification}

/**
 *
 * @author stormcat24
 */
class PathHelperSpec extends Specification with Tables with SpecSupport {

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

    "getRelativePath" in {
      Paths.get("/path1/path2/path3").getRelativePath(Paths.get("/path1/")) must_== Paths.get("/path2/path3")
    }

    "hasExtension" in {

      "illegal values" in {
        Paths.get("/path1/path2").hasExtension(null) must throwA[IllegalArgumentException]
      }

      "input" in {

        "path"                   | "extension" | "expect" |
        "/path1/path2"           ! "txt"       ! false    |
        "/path1/path2.txt"       ! "txt"       ! true     |
        "/path1/path2.txt.scala" ! "txt"       ! false    |
        "/path1/path2.txt.scala" ! "scala"     ! true     |> { (path, extension, expect) =>
          Paths.get(path).hasExtension(extension) == expect
        }
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
