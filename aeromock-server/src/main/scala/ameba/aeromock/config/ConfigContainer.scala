package ameba.aeromock.config

import ameba.aeromock.AeromockConfigurationException
import ameba.aeromock.config.definition.ProjectDef
import ameba.aeromock.config.entity.{Project, UserConfig, UserConfigDef}
import ameba.aeromock.core.{CacheKey, ObjectCache}
import ameba.aeromock.data.YamlDataFileReader
import ameba.aeromock.template.TemplateServiceFactory
import ameba.aeromock.helper._

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
          .toValue() match {
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
          .toValue(userConfig.projectConfigPath, userConfig.getProjectDirectory)
        ObjectCache.store(CacheKey[Project]("project", checkSum), value)
        value
      }
      case Some(value) => value
    }
  }

  lazy val templateService = TemplateServiceFactory.create

}
