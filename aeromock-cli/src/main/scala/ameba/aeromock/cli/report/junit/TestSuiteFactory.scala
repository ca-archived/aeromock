package ameba.aeromock.cli.report.junit

import ameba.aeromock.cli.schema._
import scala.collection.JavaConverters._
import javax.xml.datatype.DatatypeFactory
import java.util.GregorianCalendar
import java.util.concurrent.atomic.AtomicInteger
import ameba.aeromock.template._
import scalaxb.DataRecord
import ameba.aeromock.cli.schema.Properties
import ameba.aeromock.cli.schema.Testsuite
import ameba.aeromock.cli.schema.Testcase
import ameba.aeromock.cli.schema.FailureType
import ameba.aeromock.template.PageValidation
import scala.Some
import ameba.aeromock.cli.schema.Property
import scalaz._
import Scalaz._

/**
 * Factory to create [[ameba.aeromock.cli.schema.Testsuite]].
 * @author stormcat24
 */
class TestsuiteFactory() {

  val hostname = "localhost"
  var id: AtomicInteger = new AtomicInteger(0)

  val datatypeFactory = DatatypeFactory.newInstance

  val properties = Properties(System.getProperties.asScala.map(p => Property(p._1, p._2)).toSeq: _*)

  def create(pageValidation: PageValidation): Testsuite = {

    val now = datatypeFactory.newXMLGregorianCalendar(new GregorianCalendar())
    val templateTest = Testcase(None, s"Check templatepath = ${pageValidation.templatePath}", pageValidation.templatePath, pageValidation.templateResult.getTime())

    val dataTestcases = pageValidation.dataAsserts.flatMap {
      case DataAsserts(successes, failures, errors) => {
        Some(
          successes.map(s => Testcase(None, s" / datapath = ${s.dataPath}", pageValidation.templatePath, s.time)) ++
          failures.map(f => {
            val detail = f.throwable match {
              case Some(value) => value.getStackTraceString
              case None => "No Exception."
            }

            val failureType = f.throwable.flatMap {
              case t => Some(t.getClass.getName())
            } | "No Exception"

            val record = DataRecord(None, Some("failure"), FailureType(detail, Option(f.message), failureType))
            Testcase(Some(record), s" / datapath = ${f.dataPath}", pageValidation.templatePath, f.time)
          }) ++
          errors.map(e => {
            val detail = e.throwable match {
              case Some(value) => value.getStackTraceString
              case None => "No Exception."
            }

            val errorType = e.throwable.flatMap {
              case t => Some(e.getClass.getName())
            } | "No Exception"

            val record = DataRecord(None, Some("error"), Error(detail, Option(e.message), errorType))
            Testcase(Some(record), s" / datapath = ${e.dataPath}", pageValidation.templatePath, e.time)
          })
        )
      }
    }

    Testsuite(
      properties = properties,
      testcase = Seq(templateTest) ++ dataTestcases.getOrElse(Seq.empty),
      systemu45out = "", // TODO
      systemu45err = "", // TODO
      name = pageValidation.templatePath,
      timestamp = now,
      hostname = hostname,
      tests = pageValidation.totalTests,
      failures = pageValidation.totalFailures,
      errors = pageValidation.totalErrors,
      time = pageValidation.totalTestTime,
      packageValue = "ameba.aeromock",
      id = id.incrementAndGet()
    )
  }
}
