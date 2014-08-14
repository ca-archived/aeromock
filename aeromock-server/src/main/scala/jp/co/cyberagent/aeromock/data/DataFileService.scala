package jp.co.cyberagent.aeromock.data

import java.util.regex.Pattern
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.core.http.Endpoint
import jp.co.cyberagent.aeromock.config.entity.Project
import java.nio.file.Path

class DataFileService(project: Project) {

  val DATAFILE_PATTERN = Pattern.compile("""^.+__([0-9A-Za-z_-]+)\..+$""")

  def getRelatedDataFiles(endpoint: Endpoint): List[DataFile] = {
    require(endpoint != null)

    val dataRootPath = project._data.root
    val pathParts = endpoint.value.split("/")

    val ownerDirectory = (dataRootPath / endpoint.value).getParent()
    val dataFiles = ownerDirectory.getChildren
      .filter(!_.isDirectory())
      .collect {
        case path if path.getFileName().toString().startsWith(pathParts.last + ".") => path
        case path if path.getFileName().toString().startsWith(pathParts.last + "__") => path
      }

    dataFiles.map(file => {
      val matcher = DATAFILE_PATTERN.matcher(file.getFileName().toString())
      matcher.matches() match {
        case false => DataFile(None, file)
        case true => DataFile(Some(matcher.group(1)), file)
      }
    })
  }

}

case class DataFile(id: Option[String], path: Path)
