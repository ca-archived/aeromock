package jp.co.cyberagent.aeromock.data

import java.nio.file.Path

import io.netty.handler.codec.http.HttpMethod
import jp.co.cyberagent.aeromock.AeromockBadUsingException
import jp.co.cyberagent.aeromock.config.Naming
import jp.co.cyberagent.aeromock.core.http.AeromockHttpRequest
import jp.co.cyberagent.aeromock.helper._

object DataPathResolver {

  val PATTERN = """^(.+)\..+$""".r

  def resolve(rootDir: Path, parsedRequest: AeromockHttpRequest, naming: Naming): Option[Path] = {
    require(rootDir != null)
    require(parsedRequest != null)

    val files = getCandidates(rootDir, parsedRequest, naming)
    files.size match {
      case 0 => {
        // GET以外の場合、GETで代替する
        if (parsedRequest.method == HttpMethod.GET) {
          None
        } else {
          resolve(rootDir, AeromockHttpRequest(parsedRequest.url, parsedRequest.queryParameters,
            parsedRequest.postData, Map.empty, HttpMethod.GET), naming)
        }
      }
      case 1 => Some(files.last)
      case size if size > 1 => throw new AeromockBadUsingException("badusing.data.duplicated", null, parsedRequest.url)
    }
  }

  private def getCandidates(rootDir: Path, parsedRequest: AeromockHttpRequest, naming: Naming): Seq[Path] = {
    val url = parsedRequest.url
    val dataId = parsedRequest.queryParameters.get(naming.dataidQuery)

    val tokens = url.split("/")
    val basePath = tokens.last match {
      case PATTERN(value) => (tokens.take(tokens.length - 1) ++ Array(s"/$value")).mkString("/", "", "")
      case _ => url
    }

    val methodCandidates = if (parsedRequest.method == HttpMethod.GET) Seq("__get", "") else Seq(s"__${parsedRequest.method.name.toLowerCase}")
    val basePaths = methodCandidates.map(rootDir + basePath + _)

    val extensions = AllowedDataType.extensions.toList
    (dataId match {
      case Some(id) => basePaths.flatMap(p => extensions.map(p + s"__$id." + _))
      case _ => basePaths.flatMap(p => extensions.map(p + "." + _))
    }).filter(_.exists)
  }

}
