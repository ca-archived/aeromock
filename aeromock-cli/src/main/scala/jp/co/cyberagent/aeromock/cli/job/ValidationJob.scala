package jp.co.cyberagent.aeromock.cli.job

import java.nio.file.Files

import jp.co.cyberagent.aeromock.AeromockBadUsingException
import jp.co.cyberagent.aeromock.cli.option.JobOperation
import jp.co.cyberagent.aeromock.cli.validation.TemplateValidator
import jp.co.cyberagent.aeromock.cli.{CliJob, Job}
import jp.co.cyberagent.aeromock.config.Project
import jp.co.cyberagent.aeromock.template.TemplateService
import scaldi.Injector

import scala.reflect.io.Directory
import scalaz._
import Scalaz._

/**
 * Job to check templates and data.
 * @author stormcat24
 * @param command [[jp.co.cyberagent.aeromock.cli.option.JobOperation]]
 */
@Job(name = "test", description = "Execute view test.")
class ValidationJob(override val command: JobOperation)(implicit inj: Injector) extends CliJob {

  val project = inject[Project]

  // templateとデータのセット
  val reportRoot = project._test.reportRoot

  // prepare directory
  val dir = Directory.apply(scala.reflect.io.Path.apply(reportRoot.toFile))
  dir.deleteRecursively()
  Files.createDirectories(reportRoot)


  /**
   * @inheritdoc
   */
  override def execute(): Int = {

    (project.template, project.data, project.naming, project.test) match {
      case (Success(Some(template)), Success(Some(data)), Success(naming), Success(test)) => // OK
        // templateとdataを操作してリクエストをつくる
        new TemplateValidator().validate(template, data, naming, test)
          // テンプレをループ
            // 紐づくデータを探す。あればリクエスト、なければ警告
      case (Success(_), Success(_), Success(_), Success(_)) => // nothing to do
      case (_, _, _, _) => // TODO error
    }

    project.ajax match {
      case Success(Some(ajax)) =>
      case Success(_) => // nothing to do
      case f @ Failure(_) => // TODO error
    }

    project.messagepack match {
      case Success(Some(messagepack)) =>
      case Success(_) => // nothing to do
      case f @ Failure(_) => // TODO error
    }

    (project.protobuf, project.data) match {
      case (Success(Some(protobuf)), Success(Some(data))) => // OK
      case (Success(_), Success(_)) => // nothing to do
      case (_, _) => // TODO error
    }

    // TODO テスト設定
    // TODO useragent
    0
  }

}
