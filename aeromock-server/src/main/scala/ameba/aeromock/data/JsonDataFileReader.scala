package ameba.aeromock.data

import java.nio.file.Path

import ameba.aeromock.util.ResourceUtil
import ameba.aeromock.{AeromockInvalidDataFileException, AeromockBadUsingException, AeromockResourceNotFoundException}
import org.json4s.JsonAST.JValue
import org.json4s.native.JsonMethods.parse
import org.json4s.{JArray, JObject, string2JsonInput}

class JsonDataFileReader extends DataFileReader {
  
  def readFile[T >: Iterable[_]](file: Path, charset: String = "UTF-8"): T = {
    ResourceUtil.readFile(file, charset) match {
      case None => throw new AeromockResourceNotFoundException(file.toString)
      case Some(json) => convertJValue(parse(json), file)
    }
  }

  private def convertJValue(jValue: JValue, path: Path): Iterable[_] = {
    jValue match {
      case o: JObject => o.values
      case a: JArray => a.arr.map(convertJValue(_, path))
      case _ => throw new AeromockInvalidDataFileException(path)
    }
  }
  
  override def deserialize[T](file: Path, `class`: Class[T], charset: String = "UTF-8"): T = {
    
    null.asInstanceOf[T]
  }

}