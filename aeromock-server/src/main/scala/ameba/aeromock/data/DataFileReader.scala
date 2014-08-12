package ameba.aeromock.data

import java.nio.file.Path


trait DataFileReader {
  
  def readFile[T >: Iterable[_]](file: Path, charset: String = "UTF-8"): T
 
  def deserialize[T](file: Path, `class`: Class[T], charset: String = "UTF-8"): T
}