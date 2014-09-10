package jp.co.cyberagent.aeromock.data

import java.nio.file.Path
import jp.co.cyberagent.aeromock.helper._

/**
 * Factory object of [[DataFileReader]]
 * @author stormcat24
 */
object DataFileReaderFactory {

  def create(file: Path): Option[DataFileReader] = {
    Option(file).filter(_.exists).map {
      case f if file.hasExtension("json") => new JsonDataFileReader
      case f if file.hasExtension("yaml") || file.hasExtension("yml") => new YamlDataFileReader
    }
  }

}
