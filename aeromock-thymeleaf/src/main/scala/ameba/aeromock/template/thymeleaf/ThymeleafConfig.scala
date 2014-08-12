package ameba.aeromock.template.thymeleaf

import ameba.aeromock.config.TemplateConfig
import ameba.aeromock.config.definition.SpecifiedTemplateDef
import scala.beans.BeanProperty
import scalaz._
import Scalaz._

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

class ThymeleafConfigDef extends SpecifiedTemplateDef[ThymeleafConfig] {
  @BeanProperty var thymeleaf: ThymeleafDetailDef = null

  override def toValue: ValidationNel[String, Option[ThymeleafConfig]] = {

    import scala.collection.JavaConverters._

    Option(thymeleaf) match {
      case None => none[ThymeleafConfig].successNel[String]
      case Some(bean) => {

        // TODO validations
        val suffixVal = Option(bean.suffix) match {
          case None => none[String].successNel[String]
          case Some(value) => value.some.successNel[String]
        }

        val characterEncodingVal = Option(bean.characterEncoding) match {
          case None => none[String].successNel[String]
          case Some(value) => value.some.successNel[String]
        }

        val templateAliasesVal = Option(bean.templateAliases) match {
          case None => none[Map[String, String]].successNel[String]
          case Some(value) => value.asScala.toMap.some.successNel[String]
        }

        val templateModeVal = Option(bean.templateMode) match {
          case None => none[String].successNel[String]
          case Some(value) => value.some.successNel[String]
        }

        val legacyHtml5TemplateModePatternsVal = Option(bean.legacyHtml5TemplateModePatterns) match {
          case None => none[List[String]].successNel[String]
          case Some(value) => value.asScala.toList.some.successNel[String]
        }

        val validXhtmlTemplateModePatternsVal = Option(bean.validXhtmlTemplateModePatterns) match {
          case None => none[List[String]].successNel[String]
          case Some(value) => value.asScala.toList.some.successNel[String]
        }

        val validXmlTemplateModePatternsVal = Option(bean.validXmlTemplateModePatterns) match {
          case None => none[List[String]].successNel[String]
          case Some(value) => value.asScala.toList.some.successNel[String]
        }

        val xhtmlTemplateModePatternsVal = Option(bean.xhtmlTemplateModePatterns) match {
          case None => none[List[String]].successNel[String]
          case Some(value) => value.asScala.toList.some.successNel[String]
        }

        val xmlTemplateModePatternsVal = Option(bean.xmlTemplateModePatterns) match {
          case None => none[List[String]].successNel[String]
          case Some(value) => value.asScala.toList.some.successNel[String]
        }

        (suffixVal |@|
          characterEncodingVal |@|
          templateAliasesVal |@|
          templateModeVal |@|
          legacyHtml5TemplateModePatternsVal |@|
          validXhtmlTemplateModePatternsVal |@|
          validXmlTemplateModePatternsVal |@|
          xhtmlTemplateModePatternsVal |@|
          xmlTemplateModePatternsVal
          ) {ThymeleafConfig(_, _, _, _, _, _, _, _, _).some}

      }
    }
  }
}

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
