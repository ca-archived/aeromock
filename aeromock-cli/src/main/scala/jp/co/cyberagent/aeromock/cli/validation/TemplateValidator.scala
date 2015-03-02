package jp.co.cyberagent.aeromock.cli.validation

import java.nio.file.{Files, Path}

import jp.co.cyberagent.aeromock.config.{Naming, Data, Template, Test}
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

import scalaz._
import Scalaz._

/**
 *
 * @author stormcat24
 */
class TemplateValidator(implicit inj: Injector) extends AnyRef with Injectable {

  def validate(template: Template, data: Data, naming: Naming, test: Test): Unit = {
    // TODO 拡張子取るためだけに取るのイケてない
    val templateService = inject[Option[TemplateService]].get

    template.contexts.foreach { context =>
      val requestRoot = s"http://${context.domain}:${context.port}"

      context.root.filterChildren(s".${templateService.extension}$$").map { templatePath =>

        val uri = templatePath.getRelativePath(template.root).withoutExtension

        val candidates = DataFileService.getRelatedDataFiles(Endpoint(uri.toString))
        val tests = candidates.map { dataFile =>

          val request = createUrl(requestRoot, uri, dataFile)
          val future = Http(request OK as.String)
          val testResult = \/.fromTryCatchNonFatal(Await.result(future, Duration.Inf)) match {
            case -\/(e) => {
              val cause = e.getCause.asInstanceOf[StatusCode]
              (false, cause.code)
            }
            case \/-(result) => (true, 200)
          }
          Map(
            "request_url" -> request.toRequest.getUrl,
            "data_file" -> dataFile.path.toString,
            "status" -> testResult._2,
            "result" -> testResult._1
          )
        }

        val result = Map(
          "template_uri" -> uri.toString,
          "tests" -> tests
        )
        writeJson(result, test.reportRoot)
      }
    }
  }

  private def createUrl(urlRoot:String, requestUri: Path, dataFile: DataFile): Req = {
    url(dataFile match {
      case DataFile(Some(id), path, method) => s"${urlRoot}${requestUri}?_dataid=${id}"
      case DataFile(None, path, method) => s"${urlRoot}${requestUri}"
    }).setHeader("User-Agent", "Aeromock Test Job")
  }

  private def writeJson(testResult: Map[String, Any], reportRoot: Path): Unit = {

    import org.json4s._
    import org.json4s.native.Serialization
    import org.json4s.native.Serialization.write
    implicit val formats = Serialization.formats(NoTypeHints)

    val templateReportRoot = reportRoot / "template"
    Files.createDirectories(templateReportRoot)

    val jsonString = write(testResult)
    println(jsonString)
  }
}
