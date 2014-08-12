package ameba.aeromock.data

import java.nio.file.Path
import ameba.aeromock.helper._

object DataFileReaderFactory {

  def create(file: Path): Option[DataFileReader] = {
    file match {
      case null => None
      case f if !f.exists => None
      case _ => {
        if (file.hasExtension("json")) {
          Some(new JsonDataFileReader)
        } else if (file.hasExtension("yaml") || file.hasExtension("yml")) {
          Some(new YamlDataFileReader)
        } else {
          None
        }
      }
    }
  }
}
