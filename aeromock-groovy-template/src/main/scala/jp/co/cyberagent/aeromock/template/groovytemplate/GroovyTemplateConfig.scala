package jp.co.cyberagent.aeromock.template.groovytemplate

import jp.co.cyberagent.aeromock.config.TemplateConfig

/**
 * Configuration of class for groovy template.
 * @author stormcat24
 */
case class GroovyTemplateConfig(
  extension: Option[String],
  mode: Option[String]
) extends TemplateConfig
