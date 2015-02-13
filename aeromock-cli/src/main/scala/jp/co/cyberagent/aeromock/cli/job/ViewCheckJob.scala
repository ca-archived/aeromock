package jp.co.cyberagent.aeromock.cli.job

import java.nio.file.Files

import jp.co.cyberagent.aeromock.AeromockBadUsingException
import jp.co.cyberagent.aeromock.cli.option.JobOperation
import jp.co.cyberagent.aeromock.cli.{CliJob, Job}
import jp.co.cyberagent.aeromock.config.Project
import jp.co.cyberagent.aeromock.template.TemplateService
import scaldi.Injector

import scala.reflect.io.Directory

/**
 * Job to check templates and data.
 * @author stormcat24
 * @param command [[jp.co.cyberagent.aeromock.cli.option.JobOperation]]
 */
@Job(name = "test", description = "Execute view test.")
class ViewCheckJob(override val command: JobOperation)(implicit inj: Injector) extends CliJob {

  val project = inject[Project]

  // templateとデータのセット
  val template = project._template
  val dataRoot = project._data
  val reportRoot = project._test.reportRoot

  // prepare directory
  val dir = Directory.apply(scala.reflect.io.Path.apply(reportRoot.toFile))
  dir.deleteRecursively()
  Files.createDirectories(reportRoot)


  /**
   * @inheritdoc
   */
  override def execute(): Int = {
    // TODO project.yamlを見て、リクエストを生成
      // TODO template
      // TODO ajax
      // TODO protobuf, messagepack
    // TODO テスト設定
      // TODO useragent
    0
  }



}
