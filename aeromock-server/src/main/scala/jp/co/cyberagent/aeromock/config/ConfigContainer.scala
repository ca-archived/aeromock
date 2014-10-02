package jp.co.cyberagent.aeromock.config

import jp.co.cyberagent.aeromock.config.definition.ProjectDef
import jp.co.cyberagent.aeromock.config.definition.UserConfigDef
import jp.co.cyberagent.aeromock.AeromockConfigurationException
import jp.co.cyberagent.aeromock.core.{CacheKey, ObjectCache}
import jp.co.cyberagent.aeromock.data.YamlDataFileReader
import jp.co.cyberagent.aeromock.template.TemplateServiceFactory
import jp.co.cyberagent.aeromock.helper._

import scalaz._

/**
 * Stores configuration.
 * <p>Lifecycle of this instance is request scope.
 * @author stromcat24
 */
class ConfigContainer {

  val yamlReader = new YamlDataFileReader()

  lazy val userConfig = {
    val configFile = ServerOptionRepository.configFile
    val checkSum = configFile.toCheckSum
    ObjectCache.get(CacheKey[UserConfig]("userConfig", checkSum)) match {
      case None => {
        yamlReader.deserialize(configFile, classOf[UserConfigDef])
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

  lazy val project = {
    val checkSum = userConfig.projectConfigPath.toCheckSum
    ObjectCache.get(CacheKey[Project]("project", checkSum)) match {
      case None => {
        val value = yamlReader.deserialize(userConfig.projectConfigPath, classOf[ProjectDef])
          .toValue(userConfig.projectConfigPath, userConfig.projectDirectory)
        ObjectCache.store(CacheKey[Project]("project", checkSum), value)
        value
      }
      case Some(value) => value
    }
  }

  lazy val templateService = TemplateServiceFactory.create

}
