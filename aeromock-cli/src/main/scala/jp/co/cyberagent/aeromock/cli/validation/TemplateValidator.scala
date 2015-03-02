package jp.co.cyberagent.aeromock.cli.validation

import java.nio.file.Path

import jp.co.cyberagent.aeromock.config.{Naming, Data, Template}
import jp.co.cyberagent.aeromock.core.http.Endpoint
import jp.co.cyberagent.aeromock.data.DataFileService
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.template.TemplateService
import jp.co.cyberagent.aeromock.server.DataFile
import scaldi.{Injector, Injectable}
import dispatch._
import Defaults._

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

/**
 *
 * @author stormcat24
 */
class TemplateValidator(implicit inj: Injector) extends AnyRef with Injectable {

  def validate(template: Template, data: Data, naming: Naming): Unit = {
    // TODO 拡張子取るためだけに取るのイケてない
    val templateService = inject[Option[TemplateService]].get

    template.contexts.foreach { context =>
      val requestRoot = s"http://${context.domain}:${context.port}"

      context.root.filterChildren(s".${templateService.extension}$$").map { templatePath =>

        val uri = templatePath.getRelativePath(template.root).withoutExtension

        val candidates = DataFileService.getRelatedDataFiles(Endpoint(uri.toString))
        // URL構築
        candidates.map { dataFile =>
//          dataFile.
          val request = createUrl(requestRoot, uri, dataFile)
          val future = Http(request > as.String)
          future onComplete {
            case Success(result) => println(result)
            case Failure(f) => {
              throw f
            }
          }
//          val result = Await.result(future, Duration.Inf)
//          println(result)

          // HTTP client
        }
        // Request
        // 結果
        // JSON構築
        // テスト１件ずつJSONだした方がよさげ
      }
    }
  }

  private def createUrl(urlRoot:String, requestUri: Path, dataFile: DataFile): Req = {
    url(dataFile match {
      case DataFile(Some(id), path, method) => s"${urlRoot}${requestUri}?_dataid=${id}"
      case DataFile(None, path, method) => s"${urlRoot}${requestUri}"
    }).setHeader("User-Agent", "Aeromock Test Job")
  }
}
