package jp.co.cyberagent.aeromock.template.thymeleaf

import scala.beans.BeanProperty

/**
 *
 * @author stormcat24
 */
class ThymeleafDetailDef {
  @BeanProperty var suffix: String = null
  @BeanProperty var characterEncoding: String = null
  @BeanProperty var templateAliases: java.util.Map[String, String] = null
  @BeanProperty var templateMode: String = null
  @BeanProperty var legacyHtml5TemplateModePatterns: java.util.List[String] = null
  @BeanProperty var validXhtmlTemplateModePatterns: java.util.List[String] = null
  @BeanProperty var validXmlTemplateModePatterns: java.util.List[String] = null
  @BeanProperty var xhtmlTemplateModePatterns: java.util.List[String] = null
  @BeanProperty var xmlTemplateModePatterns: java.util.List[String] = null

}
