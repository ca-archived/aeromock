package jp.co.cyberagent.aeromock.data

import java.util.regex.Pattern
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.core.http.Endpoint
import jp.co.cyberagent.aeromock.config.entity.Project
import java.nio.file.Path

class DataFileService(project: Project) {

  val EXTENSION_PATTERN = ("""^.+\.""" + AllowedDataType.extensions.mkString("(", "|", ")") + "$").r
  val DATAFILE_PATTERN = Pattern.compile("""^.+__(\w+)$""")

  def getRelatedDataFiles(endpoint: Endpoint): List[DataFile] = {
    require(endpoint != null)

    val dataRootPath = project._data.root
    val pathParts = endpoint.value.split("/")

    val ownerDirectory = (dataRootPath / endpoint.value).getParent()

    ownerDirectory
      .getChildren
      .filter(!_.isDirectory())
      .filter(f => EXTENSION_PATTERN.findAllMatchIn(f.getFileName().toString).nonEmpty)
      .collect {
        case path if path.getFileName().toString().startsWith(pathParts.last + ".") => path
        case path if path.getFileName().toString().startsWith(pathParts.last + "__") => path
      }
      .map(file => {
        val name = file.withoutExtension.toString
        val matcher = DATAFILE_PATTERN.matcher(name)

        // TODO ^.+__(get|post|put|delete)__(\w+)$
        // TODO ^.+__(\w+)$
        // TODO NONE

        matcher.matches() match {
          case false => DataFile(None, file)
          case true => DataFile(Some(matcher.group(1)), file)
        }
    })
  }

}

case class DataFile(id: Option[String], path: Path)
