package jp.co.cyberagent.aeromock.cli.validation

import java.io.{FileOutputStream, OutputStreamWriter}
import java.nio.file.{Files, Path}

import scalaj.http._

import jp.co.cyberagent.aeromock.config.{Data, Naming, Template, Test}
import jp.co.cyberagent.aeromock.core.http.Endpoint
import jp.co.cyberagent.aeromock.data.DataFileService
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.server.DataFile
import jp.co.cyberagent.aeromock.template.TemplateService
import org.slf4j.LoggerFactory
import scaldi.{Injectable, Injector}

/**
 *
 * @author stormcat24
 */
class TemplateValidator(implicit inj: Injector) extends AnyRef with Injectable {

  val log = LoggerFactory.getLogger(this.getClass)

  def validate(template: Template, data: Data, naming: Naming, test: Test): TestSummary = {

    var numTemplate = 0
    var numSuccess = 0
    var numSkip = 0
    var numFailed = 0

    val templateService = inject[Option[TemplateService]].get

    template.contexts.foreach { context =>
      val requestRoot = s"http://${context.domain}:${context.port}"

      context.root.filterChildren(s".${templateService.extension}$$").map { templatePath =>

        val uri = templatePath.getRelativePath(template.root).withoutExtension

        println(s"[Test] ${uri}")

        val candidates = DataFileService.getRelatedDataFiles(Endpoint(uri.toString))

        if (candidates.isEmpty) {
          println(s"        └ [${blue("SKIP")}] Not found data file")
          numSkip += 1

        } else {

          val tests = candidates.zipWithIndex.map { case (dataFile, index) =>
            val connector = if (index < candidates.size - 1) "├" else "└"
            val request = createUrl(requestRoot, uri, dataFile)
            val response = request.asString

            val result = if (response.code < 300) {
              numSuccess += 1
              (s"[${green("SUCCESS")}]", true)
            } else {
              numFailed += 1
              (s"[${red("FAILED")}]", false)
            }

            println(s"        $connector ${result._1} data_path = ${dataFile.path}")
            Map(
              "request_url" -> request.url,
              "data_file" -> dataFile.path.toString,
              "status" -> response.code,
              "result" -> result._2
            )
          }

          val result = Map(
            "template_uri" -> uri.toString,
            "tests" -> tests
          )
          writeJson(result, test.reportRoot, uri)
        }

        numTemplate += 1

      }
    }

    println( s"""
        |****************************
        |*   Test Result Summary    *
        |****************************
        | Total Template: ${numTemplate}
        | ${green("SUCCESS")}: ${numSuccess}
        | ${red("FAILED")}: ${numFailed}
        | ${blue("SKIP")}: ${numSkip}
      """.stripMargin)

    TestSummary(numTemplate, numSuccess, numFailed, numSkip)
  }

  private def createUrl(urlRoot:String, requestUri: Path, dataFile: DataFile): HttpRequest = {

    (dataFile match {
      case DataFile(Some(id), path, method) => Http(s"${urlRoot}${requestUri}").param("_dataid", id)
      case DataFile(None, path, method) => Http(s"${urlRoot}${requestUri}")
    }).header("User-Agent", "Aeromock Test Job")
  }

  private def writeJson(testResult: Map[String, Any], reportRoot: Path, templateUri: Path): Unit = {

    import org.json4s._
    import org.json4s.native.Serialization
    import org.json4s.native.Serialization.write
    implicit val formats = Serialization.formats(NoTypeHints)

    val templateReportRoot = reportRoot / "template"
    Files.createDirectories(templateReportRoot)

    val jsonString = write(testResult)
    val jsonPath = templateReportRoot / templateUri + ".json"
    val jsonPathDir = jsonPath.getParent
    Files.createDirectories(jsonPathDir)

    processResource(new OutputStreamWriter(new FileOutputStream(jsonPath.toFile), "UTF-8")) { writer =>
      writer.write(jsonString)
    }

  }
}
