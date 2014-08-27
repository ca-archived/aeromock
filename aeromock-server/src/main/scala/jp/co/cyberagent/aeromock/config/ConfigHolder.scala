package jp.co.cyberagent.aeromock.config

object ConfigHolder {

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
