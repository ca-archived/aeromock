package ameba.aeromock.config

import ameba.aeromock.AeromockConfigurationException
import ameba.aeromock.config.definition.ProjectDef
import ameba.aeromock.config.entity.{Project, UserConfigDef, UserConfig}
import ameba.aeromock.core.{CacheKey, ObjectCache}
import ameba.aeromock.data.YamlDataFileReader
import ameba.aeromock.helper._

import scalaz.{Success, Failure}

class ConfigHolder {

}


object ConfigHolder {

  val yamlReader = new YamlDataFileReader()

  val threadLocal = new ThreadLocal[ConfigContainer] {

    override def initialValue(): ConfigContainer = null

    override def get(): ConfigContainer = super.get

    override def set(holder: ConfigContainer): Unit = super.set(holder)

    override def remove(): Unit = super.remove

  }

  def initialize(): Unit = {
    threadLocal.remove()
    threadLocal.set(new ConfigContainer)
  }

  def getUserConfig = threadLocal.get().userConfig

  def getProject = threadLocal.get().project

  def getTemplateService = threadLocal.get().templateService

}
