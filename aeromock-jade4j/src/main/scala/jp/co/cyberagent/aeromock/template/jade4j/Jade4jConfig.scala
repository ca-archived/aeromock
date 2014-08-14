package jp.co.cyberagent.aeromock.template.jade4j

import de.neuland.jade4j.Jade4J
import jp.co.cyberagent.aeromock.config.TemplateConfig

/**
 * Configuration of class for jade4j.
 * @author stormcat24
 */
case class Jade4jConfig(
  extension: Option[String],
  mode: Option[Jade4J.Mode],
  prettyPrint: Option[Boolean]
) extends TemplateConfig
