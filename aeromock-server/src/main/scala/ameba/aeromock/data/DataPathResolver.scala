package ameba.aeromock.data

import java.io.File
import java.util.regex.Pattern
import ameba.aeromock.AeromockBadUsingException
import ameba.aeromock.AeromockSystemException
import ameba.aeromock.core.http.ParsedRequest
import java.nio.file.Path
import ameba.aeromock.helper._
import java.nio.file.Paths
import ameba.aeromock.config.ConfigHolder

object DataPathResolver {

  val pattern = Pattern.compile("""^(\/.+)\..+""")

  val allowedFormats = List(AllowedDataType.JSON, AllowedDataType.YAML)

  def resolve(rootDir: Path, parsedRequest: ParsedRequest): Option[Path] = {
    require(rootDir != null)
    require(parsedRequest != null)

    val files = getCandidates(rootDir, parsedRequest).map(Paths.get(_)).filter(_.exists)
    files.size match {
      case 0 => None
      case 1 => Some(files.last)
      case size if size > 1 => throw new AeromockBadUsingException("badusing.data.duplicated", null, parsedRequest.url)
    }
  }

  private def getCandidates(rootDir: Path, parsedRequest: ParsedRequest): List[String] = {
    val naming = ConfigHolder.getProject._naming
    val url = parsedRequest.url
    val dataId = parsedRequest.queryParameters.get(naming.dataidQuery)

    val basePath = pattern.matcher(url) match {
      case m if m.find() => m.group(1)
      case _ => url
    }

    val extensions = allowedFormats.flatMap(_.extensions)
    (dataId match {
      case None => extensions.map(rootDir + basePath + "." + _)
      case Some(id: String) if id.isEmpty() => extensions.map(rootDir + basePath + "." + _)
      case Some(id) => extensions.map(rootDir + basePath + s"__$id." + _)
    }).map(_.toString)
  }

}
