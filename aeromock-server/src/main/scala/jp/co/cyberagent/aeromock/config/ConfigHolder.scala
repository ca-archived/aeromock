package jp.co.cyberagent.aeromock.config

import jp.co.cyberagent.aeromock.config.definition.ProjectDef
import jp.co.cyberagent.aeromock.config.entity.{Project, UserConfigDef, UserConfig}
import jp.co.cyberagent.aeromock.core.{CacheKey, ObjectCache}
import jp.co.cyberagent.aeromock.data.YamlDataFileReader
import jp.co.cyberagent.aeromock.AeromockConfigurationException
import jp.co.cyberagent.aeromock.helper._

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
