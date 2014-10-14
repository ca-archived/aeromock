package jp.co.cyberagent.aeromock.data

import java.nio.file.Path

import io.netty.handler.codec.http.HttpMethod
import jp.co.cyberagent.aeromock.config.Project
import jp.co.cyberagent.aeromock.core.http.Endpoint
import jp.co.cyberagent.aeromock.helper._
import scaldi.{Injectable, Injector}

object DataFileService extends AnyRef with Injectable {

  val EXTENSION_PATTERN = ("""^.+\.""" + AllowedDataType.extensions.mkString("(", "|", ")") + "$").r
  val METHOD_NUMBER_PATTERN = """^.+__(options|get|head|post|put|patch|delete|trace|connect)__(\w+)$""".r
  val METHOD_ONLY_PATTERN = """^.+__(options|get|head|post|put|patch|delete|trace|connect)$""".r
  val NUMBER_PATTERN = """^.+__(\w+)$""".r

  def getRelatedDataFiles(endpoint: Endpoint)(implicit inj: Injector): List[DataFile] = {
    require(endpoint != null)

    val project = inject[Project]

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
        name match {
          case METHOD_NUMBER_PATTERN(method, id) => DataFile(Some(id), file, HttpMethod.valueOf(method.toUpperCase))
          case METHOD_ONLY_PATTERN(method) => DataFile(None, file, HttpMethod.valueOf(method.toUpperCase))
          case NUMBER_PATTERN(id) => DataFile(Some(id), file)
          case _ => DataFile(None, file)
        }
    })
  }

}

case class DataFile(id: Option[String], path: Path, method: HttpMethod = HttpMethod.GET)
