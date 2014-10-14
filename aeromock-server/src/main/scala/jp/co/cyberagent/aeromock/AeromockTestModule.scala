package jp.co.cyberagent.aeromock

import java.nio.file.Path

import jp.co.cyberagent.aeromock.config.definition.ProjectDef
import jp.co.cyberagent.aeromock.config.{UserConfig, Project, ServerOption}
import jp.co.cyberagent.aeromock.data.YamlDataFileReader
import jp.co.cyberagent.aeromock.helper._

/**
 *
 * @author stormcat24
 */
trait AeromockTestModule extends AeromockModule {

  override val serverOption = new ServerOption(None, None)

  val projectDefArround: ProjectDef => Unit
  val projectConfigPath: Path

  override def createUserConfig: UserConfig = UserConfig(projectConfigPath, None)

  override def createProject(): Project = {

    val projectDef = if (projectConfigPath.exists) {
      inject[YamlDataFileReader]
        .deserialize(projectConfigPath, classOf[ProjectDef])
    } else {
      new ProjectDef
    }

    projectDefArround(projectDef)
    projectDef.toValue(projectConfigPath)
  }

}
