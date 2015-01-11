package jp.co.cyberagent.aeromock.core.http

import org.specs2.mutable.{Tables, Specification}

/**
 *
 * @author stormcat24
 */
class ParsedRequestSpec extends Specification with Tables {

  "ParsedRequest" should {

    "url is null" in {
      ParsedRequest(null, Map.empty, Map.empty) must throwA[IllegalArgumentException]
    }

    "apply url" in {

      "url"                | "expect" |
      ""                   ! "/index" |
      " "                  ! "/index" |
      "/"                  ! "/index" |
      "/path1"             ! "/path1" |
      "/path1/"            ! "/path1/index" |
      "/path1/index"       ! "/path1/index" |
      "/path1/path2"       ! "/path1/path2" |
      "/path1/path2/"      ! "/path1/path2/index" |
      "/path1/path2/index" ! "/path1/path2/index" |> {
        (url, expect) => ParsedRequest(url, Map.empty, Map.empty).url must_== expect
      }
    }

    "apply map" in {

      val url = "/test"
      val queryParameters = Map("qs1" -> "1", "qs2" -> "2")
      val postData = Map("fd1" -> "1", "fd2" -> "2")

      val actual = ParsedRequest(url, queryParameters, postData)
      actual.url must_== url
      actual.queryParameters must_== Map("qs1" -> "1", "qs2" -> "2")
      actual.postData must_== Map("fd1" -> "1", "fd2" -> "2")
    }
  }

}
