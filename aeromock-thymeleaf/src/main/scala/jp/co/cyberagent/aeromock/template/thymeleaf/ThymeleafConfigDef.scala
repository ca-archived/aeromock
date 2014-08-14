package jp.co.cyberagent.aeromock.template.thymeleaf

import jp.co.cyberagent.aeromock.config.definition.SpecifiedTemplateDef

import scala.beans.BeanProperty
import scalaz.Scalaz._
import scalaz._

/**
 *
 * @author stormcat24
 */
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
