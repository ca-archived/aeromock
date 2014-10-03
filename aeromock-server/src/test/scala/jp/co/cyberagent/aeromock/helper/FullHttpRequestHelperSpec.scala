package jp.co.cyberagent.aeromock.helper

import io.netty.handler.codec.http.{HttpMethod, HttpVersion, DefaultFullHttpRequest}
import jp.co.cyberagent.aeromock.SpecSupport
import org.specs2.mutable.{Tables, Specification}

/**
 *
 * @author stormcat24
 */
class FullHttpRequestHelperSpec extends Specification with Tables with SpecSupport {

  "FullHttpRequestHelper" should {

    "decoded" in {
      "uri"                         | "expect"                |
      "/path1"                      ! "/path1"                |
      "/path1?qs=hoge"              ! "/path1?qs=hoge"        |
      "/path1/path2"                ! "/path1/path2"          |
      "/path1/path2?qs=hoge"        ! "/path1/path2?qs=hoge"  |> { (uri, expect) =>
        val request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri)
        request.decoded must_== expect
      }
    }

    "requestUri" in {
      "uri"                         | "expect"                |
      "/path1"                      ! "/path1"                |
      "/path1?qs=hoge"              ! "/path1"                |
      "/path1/path2"                ! "/path1/path2"          |
      "/path1/path2?qs=hoge"        ! "/path1/path2"          |> { (uri, expect) =>
        val request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri)
        request.requestUri must_== expect
      }
    }

    "queryString" in {
      "uri"                              | "expect"              |
      "/path1"                           ! ""                    |
      "/path1?qs=hoge"                   ! "qs=hoge"             |
      "/path1?qs1=hoge1&qs2=hoge2"       ! "qs1=hoge1&qs2=hoge2" |
      "/path1/path2"                     ! ""                    |
      "/path1/path2?qs=hoge"             ! "qs=hoge"             |
      "/path1/path2?qs1=hoge1&qs2=hoge2" ! "qs1=hoge1&qs2=hoge2" |> { (uri, expect) =>
        val request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri)
        request.queryString must_== expect
      }
    }

    "extension" in {
      "uri"                                    | "expect"              |
      "/path1"                                 ! beNone                |
      "/path1?qs=hoge"                         ! beNone                |
      "/path1.txt?qs=hoge"                     ! beSome("txt")         |
      "/path1.txt?qs1=hoge1&qs2=hoge2"         ! beSome("txt")         |
      "/path1/path2"                           ! beNone                |
      "/path1/path2?qs=hoge"                   ! beNone                |
      "/path1/path2.txt?qs=hoge"               ! beSome("txt")         |
      "/path1/path2.txt?qs1=hoge1&qs2=hoge2"   ! beSome("txt")         |> { (uri, expect) =>
        val request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri)
        request.extension must expect
      }
    }
  }
}
