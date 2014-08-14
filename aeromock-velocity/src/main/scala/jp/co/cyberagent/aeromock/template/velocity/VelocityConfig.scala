package jp.co.cyberagent.aeromock.template.velocity

import jp.co.cyberagent.aeromock.config.TemplateConfig

/**
 * Configuration of class for Velocity.
 * @author stormcat24
 */
case class VelocityConfig(
  extension: Option[String]) extends TemplateConfig
