package jp.co.cyberagent.aeromock.cli

import java.nio.file.Path

import jp.co.cyberagent.aeromock.AeromockTestModule
import jp.co.cyberagent.aeromock.cli.job.ViewCheckJob
import jp.co.cyberagent.aeromock.cli.option.CliCommand
import jp.co.cyberagent.aeromock.config.definition.ProjectDef
import jp.co.cyberagent.aeromock.test.SpecSupport
import org.specs2.mutable.{Tables, Specification}

/**
 *
 * @author stormcat24
 */
class CliJobSelectorSpec extends Specification with Tables with SpecSupport {

  "select" in {
    implicit val module = new AeromockTestModule {
      override val projectConfigPath: Path = getResourcePath(".").resolve("test/project.yaml").toRealPath()
      override val projectDefArround = (projectDef: ProjectDef) => {}
    } ++ new AeromockCliModule

    val command = CliCommand(Array("test"))

    val selector = inject[CliJobSelector]
    selector.select(command).getClass must_== classOf[ViewCheckJob]
  }
}
