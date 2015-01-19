package jp.co.cyberagent.aeromock.helper

import io.netty.buffer.Unpooled
import io.netty.handler.codec.http.{HttpHeaders, DefaultFullHttpRequest}
import io.netty.handler.codec.http.HttpVersion._
import io.netty.handler.codec.http.HttpMethod._
import io.netty.util.CharsetUtil
import jp.co.cyberagent.aeromock.AeromockInvalidRequestException
import jp.co.cyberagent.aeromock.test.SpecSupport
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
        val request = new DefaultFullHttpRequest(HTTP_1_1, GET, uri)
        request.decoded must_== expect
      }
    }

    "requestUri" in {
      "uri"                         | "expect"                |
      "/path1"                      ! "/path1"                |
      "/path1?qs=hoge"              ! "/path1"                |
      "/path1/path2"                ! "/path1/path2"          |
      "/path1/path2?qs=hoge"        ! "/path1/path2"          |> { (uri, expect) =>
        val request = new DefaultFullHttpRequest(HTTP_1_1, GET, uri)
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
        val request = new DefaultFullHttpRequest(HTTP_1_1, GET, uri)
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
        val request = new DefaultFullHttpRequest(HTTP_1_1, GET, uri)
        request.extension must expect
      }
    }

    "parsedRequest of POST application/json" in {

      val testJson =
        """
          |{
          |  "hoge": "fuga",
          |  "nest": {
          |    "param1": "value1",
          |    "param2": "value2"
          |  }
          |  "array": [1, 2]
          |}
        """.stripMargin

      val jsonExpect = Map(
        "hoge" -> "fuga",
        "nest" -> Map(
          "param1" -> "value1",
          "param2" -> "value2"
        ),
        "array" -> List(1, 2)
      )

      "uri"            | "content_type"             | "input"           | "params"             |  "postData"  |
      "/path1"         ! None                       ! None              ! Map.empty            !  Map.empty   |
      "/path1?qs=hoge" ! Option("application/json") ! Option("")        ! Map("qs" -> "hoge")  !  Map.empty   |
      "/path1"         ! Option("application/json") ! Option("")        ! Map.empty            !  Map.empty   |
      "/path1"         ! Option("application/json") ! Option(testJson)  ! Map.empty            !  jsonExpect  |> { (uri, contentType, input, params, postData) =>

        val request = new DefaultFullHttpRequest(HTTP_1_1, POST, uri)
        contentType.map(request.headers.add(HttpHeaders.Names.CONTENT_TYPE, _))
        input.map { s =>
          val buf = Unpooled.wrappedBuffer(s.getBytes(CharsetUtil.UTF_8))
          request.content.writeBytes(buf)
        }

        request.queryParameters must_== params
        request.postData must_== postData
      }
    }

    "parsedRequest of POST application/json, illegal json" in {
      "uri"            | "content_type"             | "input"           |
      "/path1"         ! Option("application/json") ! Option("cannot parse")  |> { (uri, contentType, input) =>
        val request = new DefaultFullHttpRequest(HTTP_1_1, POST, uri)
        contentType.map(request.headers.add(HttpHeaders.Names.CONTENT_TYPE, _))
        input.map { s =>
          val buf = Unpooled.wrappedBuffer(s.getBytes(CharsetUtil.UTF_8))
          request.content.writeBytes(buf)
        }

        request.postData must throwA [AeromockInvalidRequestException]
      }
    }
  }
}
