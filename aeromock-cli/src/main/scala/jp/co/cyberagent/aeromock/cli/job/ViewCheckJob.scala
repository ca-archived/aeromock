package jp.co.cyberagent.aeromock.cli.job

import java.io.FileWriter
import java.nio.file.{Files, Path}

import jp.co.cyberagent.aeromock.AeromockBadUsingException
import jp.co.cyberagent.aeromock.cli.option.JobOperation
import jp.co.cyberagent.aeromock.cli.report.junit.TestsuiteFactory
import jp.co.cyberagent.aeromock.cli.{CliJob, Job}
import jp.co.cyberagent.aeromock.config.Project
import jp.co.cyberagent.aeromock.core.http.Endpoint
import jp.co.cyberagent.aeromock.template.{DataAsserts, TemplateContexts, PageValidation, TemplateService}
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.util.ResourceUtil
import org.slf4j.LoggerFactory

import scala.reflect.io.Directory

/**
 * Job to check templates and data.
 * @author stormcat24
 * @param operation [[jp.co.cyberagent.aeromock.cli.option.JobOperation]]
 */
@Job(name = "test", description = "Execute view test.")
class ViewCheckJob(operation: JobOperation, project: Project,
                   templateService: Option[TemplateService]) extends CliJob(operation, project, templateService) {

  val log = LoggerFactory.getLogger(this.getClass())

  val service = templateService match {
    case Some(service) => service
    case None => throw new AeromockBadUsingException("TemplateService is disabled. Please Check template.serviceClass. ")
  }

  val template = project._template
  val dataRoot = project._data.root
  val reportRoot = project._test.reportRoot

  // prepare directory
  val dir = Directory.apply(scala.reflect.io.Path.apply(reportRoot.toFile))
  dir.deleteRecursively()
  Files.createDirectories(reportRoot)

  val testsuiteFactory = new TestsuiteFactory

  /**
   * @inheritdoc
   */
  override def execute(): Int = {

    import jp.co.cyberagent.aeromock.template.AssertionResultType._

    val contexts = TemplateContexts.getAllContexts(project)
    val result = contexts.flatMap(context => validateDirectory(context.root, context.domain))
    val resultGroup = result.groupBy(_.getAssertionResultType())

    println( s"""
        |****************************
        |* Success Tests as follows *
        |****************************
      """.stripMargin)
    resultGroup.get(SUCCESS) match {
      case Some(tests) => tests.foreach(test => println(s"[${test.templateResult}] ${test.templatePath}"))
      case None => println("Nothing")
    }

    println( s"""
        |****************************
        |* Test has problem as follows *
        |****************************
      """.stripMargin)

    result.filter(_.getAssertionResultType() != SUCCESS).foreach { test =>
      println(s" Template:[${test.templateResult}] ${test.templatePath}")
      test match {
        case PageValidation(templatePath, templateResult, Some(DataAsserts(_, failures, errors))) => {

          errors.foreach { error =>
            println(s"""|  [${error}] ${error.dataPath}
              |    Detail:
              |      ${error.message}
              |""".stripMargin)
          }

          failures.foreach { failure =>
            println(s"""|  [${failure}] ${failure.dataPath}
              |    Detail:
              |      ${failure.message}
              |""".stripMargin)
          }
        }
        case _ =>
      }
    }


    val summary = result.foldLeft((0, 0, 0, 0))((l, r) => {
      // _1: total, _2: success, _3: error, _4: failure
      (l._1 + r.totalTests, l._2 + r.totalSuccesses, l._3 + r.totalErrors, l._4 + r.totalFailures)
    })

    println( s"""
        |****************************
        |*   Test Result Summary    *
        |****************************
        | Total: ${summary._1}
        | ${green("Success")}: ${summary._2}
        | ${red("Error")}: ${summary._3}
        | ${blue("Failure")}: ${summary._4}
      """.stripMargin)

    0
  }

  val XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
  val defaultNamespaces = scalaxb.toScope(Array.empty[(Option[String], String)]: _*)
  val xmlPrinter = new scala.xml.PrettyPrinter(200, 4)

  private def validateDirectory(directory: Path, domain: String): List[PageValidation] = {
    import scalaxb._

    directory.getChildren().flatMap {
      case d if d.isDirectory() => validateDirectory(d, domain)
      case path => {
        val relativePath = path.getRelativePath(template.root)
        log.info(s"Testing ${relativePath.toString}")
        val endpoint = Endpoint(relativePath.withoutExtension().toString)
        List(service.validateData(endpoint, domain) { pageValidation =>

          val xmlFile = reportRoot / s"TEST${endpoint.raw.replace("/", "__")}.xml"
          val testsuite = testsuiteFactory.create(pageValidation)
          val xml = toXML(testsuite, None, "testsuite", defaultNamespaces)

          ResourceUtil.processResrouce(new FileWriter(xmlFile.toFile)) { writer =>
            writer.write(XML_DECLARATION + xmlPrinter.format(xml(0)))
          }

        })
      }
    }
  }

}
