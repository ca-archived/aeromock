package jp.co.cyberagent.aeromock.template.thymeleaf

import jp.co.cyberagent.aeromock.config.TemplateConfig

/**
 * Configuration of class for thymeleaf.
 * @author stormcat24
 */
case class ThymeleafConfig(
  suffix: Option[String],
  characterEncoding: Option[String],
  templateAliases: Option[Map[String, String]],
  templateMode: Option[String],
  legacyHtml5TemplateModePatterns: Option[List[String]],
  validXhtmlTemplateModePatterns: Option[List[String]],
  validXmlTemplateModePatterns: Option[List[String]],
  xhtmlTemplateModePatterns: Option[List[String]],
  xmlTemplateModePatterns: Option[List[String]]
) extends TemplateConfig
