package jp.co.cyberagent.aeromock.data

import java.nio.file.Paths

import io.netty.handler.codec.http.HttpMethod._
import jp.co.cyberagent.aeromock.AeromockBadUsingException
import jp.co.cyberagent.aeromock.config.entity.Naming
import jp.co.cyberagent.aeromock.core.http.ParsedRequest
import org.specs2.mutable.{Specification, Tables}


/**
 *
 * @author stormcat24
 */
class DataPathResolverSpec extends Specification with Tables {

  val projectRootPath = Paths.get(Thread.currentThread().getContextClassLoader().getResource("data/DataFileService/").getPath)
  val dataRootPath = projectRootPath.resolve("./data")

  "resolve" should {

    "duplicated" in {
      "method" | "params"                   |
      GET      ! Map.empty[String, String]  |
      POST     ! Map.empty[String, String]  |
      DELETE   ! Map.empty[String, String]  |
      GET      ! Map("_dataid" -> "2")      |> { (method, params) =>

        val request = ParsedRequest("/duplicated", params, Map.empty, method)
        DataPathResolver.resolve(dataRootPath, request, Naming()) must throwA[AeromockBadUsingException]
      }
    }

    "none" in {
      "path"     | "method" | "params"                   |
      "/none"    ! GET      ! Map.empty[String, String]  |
      "/none"    ! POST     ! Map.empty[String, String]  |
      "/none"    ! PUT      ! Map.empty[String, String]  |
      "/none"    ! DELETE   ! Map.empty[String, String]  |
      "/none"    ! GET      ! Map.empty[String, String]  |> { (path, method, params) =>

        val request = ParsedRequest(path, params, Map.empty, method)
        DataPathResolver.resolve(dataRootPath, request, Naming()) must beNone
      }
    }

    "other" in {
      "method" | "params"                     | "expect"               |
       GET     ! Map.empty[String, String]    ! "other.yaml"           | // hit
       POST    ! Map.empty[String, String]    ! "other__post.yaml"     | // hit
       PUT     ! Map.empty[String, String]    ! "other.yaml"           | // not hit, use default
       DELETE  ! Map.empty[String, String]    ! "other.yaml"           |> { (method, params, expect) =>

        val request = ParsedRequest("/other", params, Map.empty, method)
        DataPathResolver.resolve(dataRootPath, request, Naming()) must beSome(dataRootPath.resolve(expect))
      }
    }

    "1st hierarchy" in {
      "method" | "params"                     | "expect"               |
      GET      ! Map.empty[String, String]    ! "path1.yaml"           |
      GET      ! Map("_dataid" -> "2")        ! "path1__2.yaml"        |
      GET      ! Map("_dataid" -> "4")        ! "path1__4.json"        |
      DELETE   ! Map.empty[String, String]    ! "path1__delete.yaml"   |
      POST     ! Map.empty[String, String]    ! "path1__post.yaml"     |
      POST     ! Map("_dataid" -> "2")        ! "path1__post__2.yaml"  |
      PUT      ! Map.empty[String, String]    ! "path1__put.yaml"      |
      GET      ! Map("_dataid" -> "xx")       ! "path1__xx.yaml"       |> { (method, params, expect) =>

        val request = ParsedRequest("/path1", params, Map.empty, method)
        DataPathResolver.resolve(dataRootPath, request, Naming()) must beSome(dataRootPath.resolve(expect))
      }
    }

    "2nd hierarchy" in {
      "method" | "params"                     | "expect"               |
      GET      ! Map.empty[String, String]    ! "path2.yaml"           |
      GET      ! Map("_dataid" -> "2")        ! "path2__2.yaml"        |
      GET      ! Map("_dataid" -> "4")        ! "path2__4.json"        |
      DELETE   ! Map.empty[String, String]    ! "path2__delete.yaml"   |
      POST     ! Map.empty[String, String]    ! "path2__post.yaml"     |
      POST     ! Map("_dataid" -> "2")        ! "path2__post__2.yaml"  |
      PUT      ! Map.empty[String, String]    ! "path2__put.yaml"      |
      GET      ! Map("_dataid" -> "xx")       ! "path2__xx.yaml"       |> { (method, params, expect) =>

        val request = ParsedRequest("/path1/path2", params, Map.empty, method)
        DataPathResolver.resolve(dataRootPath, request, Naming()) must beSome(dataRootPath.resolve(s"path1/$expect"))
      }
    }
  }

}
