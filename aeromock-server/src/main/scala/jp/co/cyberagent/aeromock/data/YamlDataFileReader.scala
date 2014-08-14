package jp.co.cyberagent.aeromock.data

import java.nio.file.Path

import jp.co.cyberagent.aeromock.helper.DeepTraversal._
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.util.ResourceUtil
import jp.co.cyberagent.aeromock.{AeromockConfigurationException, AeromockInvalidDataFileException, AeromockResourceNotFoundException}
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.representer.Representer

class YamlDataFileReader extends DataFileReader {

  override def readFile[T >: Iterable[_]](file: Path, charset: String = "UTF-8"): T = {

    ResourceUtil.read[Iterable[Any]](file, charset) match {
      case None => throw new AeromockResourceNotFoundException(file.toString)
      case Some(readFunc) => {
        readFunc(is => {
          trye(new Yaml().load(is)) match {
            case Left(e) => throw new AeromockInvalidDataFileException(file, e)
            case Right(yaml) => {
              val result = yaml match {
                case null => Map.empty
                case map: java.util.Map[_, _] => {
                  val aaa = asScalaMap(map){a => a}
                  aaa
                }
                case collection: java.util.Collection[_] => asScalaIterable(collection) {a => a}
                case _ => throw new AeromockInvalidDataFileException(file)
              }
              result
            }
          }
        })
      }
    }
  }

  val representer = new Representer
  representer.getPropertyUtils().setSkipMissingProperties(true)

  // [note] implicit ClassTagはあえて使用しない。annotationから取得したクラスの場合難しいため
  override def deserialize[T](file: Path, `class`: Class[T], charset: String = "UTF-8"): T = {

    ResourceUtil.readFile(file, charset) match {
      case None => throw new AeromockResourceNotFoundException(file.toString)
      case Some(yaml) => new Yaml(new Constructor(`class`), representer).load(yaml) match {
        case yaml: Object => yaml.asInstanceOf[T]
        case _ => {
          throw new AeromockConfigurationException(file, s"'${file.toString}' contents is illegal data format.")
        }
      }
    }
  }

}
