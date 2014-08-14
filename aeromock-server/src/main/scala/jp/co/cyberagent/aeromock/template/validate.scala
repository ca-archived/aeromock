package jp.co.cyberagent.aeromock.template

import jp.co.cyberagent.aeromock.helper._

trait AssertSuccess extends Assertable {
  override def toString(): String = green("SUCCESS")
  override def getAssertionResultType(): AssertionResultType = AssertionResultType.SUCCESS
}

trait AssertFailure extends Assertable {
  override def toString(): String = blue("FAILURE")
  override def getAssertionResultType(): AssertionResultType = AssertionResultType.FAILURE
}

trait AssertError extends Assertable {
  override def toString(): String = blue("ERROR")
  override def getAssertionResultType(): AssertionResultType = AssertionResultType.ERROR
}

sealed abstract class TemplateAssertResult(time: BigDecimal, detail: String) {
  def getDetail(): String = detail
  def getTime(): BigDecimal = time
}

case class TemplateAssertSuccess(time: BigDecimal, detail: String = "") extends TemplateAssertResult(time, detail) with AssertSuccess
case class TemplateAssertError(time: BigDecimal, detail: String) extends TemplateAssertResult(time, detail) with AssertFailure
case class TemplateAssertFailure(time: BigDecimal, detail: String) extends TemplateAssertResult(time, detail) with AssertError

case class DataAssertSuccess(dataPath: String, time: BigDecimal) extends AssertSuccess
case class DataAssertFailure(dataPath: String, time: BigDecimal, message: String, throwable: Option[Throwable]) extends AssertFailure
case class DataAssertError(dataPath: String, time: BigDecimal, message: String, throwable: Option[Throwable]) extends AssertError

case class DataAsserts(
  successes: List[DataAssertSuccess],
  failures: List[DataAssertFailure],
  errors: List[DataAssertError]
) {

  def totalTests: Int = successes.size + failures.size + errors.size
  def totalErrors: Int = errors.size
  def totalFailures: Int = failures.size
  def totalTestTime: BigDecimal = {
    failures.foldLeft(
      errors.foldLeft(
        successes.foldLeft(BigDecimal(0)){
          (l, r) => l + r.time
        }) {
        (l, r) => l + r.time
      }) {
      (l, r) => l + r.time
    }
  }
}

sealed abstract class AssertionResultType

object AssertionResultType {

  case object SUCCESS extends AssertionResultType

  case object FAILURE extends AssertionResultType

  case object ERROR extends AssertionResultType
}

trait Assertable {
  def getAssertionResultType(): AssertionResultType
}

case class PageValidation(templatePath: String, templateResult: TemplateAssertResult,
                          dataAsserts: Option[DataAsserts]) extends Assertable {

  override def getAssertionResultType(): AssertionResultType = {

    (templateResult, dataAsserts) match {
      // テンプレートOKでデータファイル無しはSUCCESSとする
      case (TemplateAssertSuccess(_, _), None) => AssertionResultType.SUCCESS
      case (TemplateAssertSuccess(_, _), Some(asserts)) => {
        if (!asserts.errors.isEmpty) {
          AssertionResultType.ERROR
        } else if (!asserts.failures.isEmpty) {
          AssertionResultType.FAILURE
        } else {
          AssertionResultType.SUCCESS
        }
      }
      case (TemplateAssertFailure(_, _), _) => AssertionResultType.FAILURE
      case (TemplateAssertError(_, _), _) => AssertionResultType.ERROR
    }
  }

  def totalTests: Int = {
    // data検証件数+1（テンプレート検証）
    dataAsserts match {
      case Some(asserts) => asserts.totalTests + 1
      case None => 1
    }
  }

  def totalSuccesses: Int = totalTests - totalErrors + totalFailures

  def totalErrors: Int = {
    val dataErrorCount = dataAsserts match {
      case Some(asserts) => asserts.totalErrors
      case None => 0
    }

    templateResult match {
      case TemplateAssertError(_, _) => dataErrorCount + 1
      case _ => dataErrorCount
    }
  }
  def totalFailures: Int = {
    val dataFailureCount = dataAsserts match {
      case Some(asserts) => asserts.totalFailures
      case None => 0
    }

    templateResult match {
      case TemplateAssertFailure(_, _) => dataFailureCount + 1
      case _ => dataFailureCount
    }
  }

  def totalTestTime: BigDecimal = {
    dataAsserts match {
      case Some(asserts) => asserts.totalTestTime + templateResult.getTime()
      case None => templateResult.getTime()
    }
  }

}
