package jp.co.cyberagent.aeromock

import java.nio.file.Path

import jp.co.cyberagent.aeromock.config.definition.{UserConfigDef, ProjectDef}
import jp.co.cyberagent.aeromock.config.{Project, UserConfig, ServerOption, ServerOptionDef}
import jp.co.cyberagent.aeromock.core.{CacheKey, ObjectCache}
import jp.co.cyberagent.aeromock.data.YamlDataFileReader
import jp.co.cyberagent.aeromock.helper._
import org.kohsuke.args4j.CmdLineParser

import scalaz.{Success, Failure}

/**
 *
 * @author stormcat24
 */
class AeromockAppModule(args: Array[String]) extends AeromockModule {

  val option = new ServerOptionDef
  val parser = new CmdLineParser(option)
  parser.parseArgument(args: _*)

  override val serverOption = ServerOption(option)

  override def createUserConfig: UserConfig = {
    val configFile = inject[Path] (identified by 'configFile)
    val checkSum = configFile.toCheckSum
    ObjectCache.get(CacheKey[UserConfig]("userConfig", checkSum)) match {
      case None => {
        inject[YamlDataFileReader].deserialize(configFile, classOf[UserConfigDef])
          .toValue(configFile) match {
          case Failure(value) => throw new AeromockConfigurationException(configFile, value)
          case Success(value) => {
            ObjectCache.store(CacheKey[UserConfig]("userConfig", checkSum), value)
            value
          }
        }
      }
      case Some(value) => value
    }
  }

  override def createProject: Project = {
    val userConfig = inject[UserConfig]
    val checkSum = userConfig.projectConfigPath.toCheckSum
    ObjectCache.get(CacheKey[Project]("project", checkSum)) match {
      case None => {
        val value = inject[YamlDataFileReader]
          .deserialize(userConfig.projectConfigPath, classOf[ProjectDef])
          .toValue(userConfig.projectConfigPath)
        ObjectCache.store(CacheKey[Project]("project", checkSum), value)
        value
      }
      case Some(value) => value
    }
  }
}
