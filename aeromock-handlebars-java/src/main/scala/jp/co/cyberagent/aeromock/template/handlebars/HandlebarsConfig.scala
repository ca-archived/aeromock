package jp.co.cyberagent.aeromock.template.handlebars

import jp.co.cyberagent.aeromock.config.TemplateConfig
import jp.co.cyberagent.aeromock.config.definition.SpecifiedTemplateDef
import scala.beans.BeanProperty
import scalaz._
import Scalaz._

/**
 * Configuration of class for Handlebars.java.
 * @author stormcat24
 */
case class HandlebarsConfig(
  suffix: Option[String],
  prettyPrint: Option[Boolean],
  stringParams: Option[Boolean],
  infiniteLoops: Option[Boolean],
  deletePartialAfterMerge: Option[Boolean],
  startDelimiter: Option[String],
  endDelimiter: Option[String]
) extends TemplateConfig

class HandlebarsConfigDef extends SpecifiedTemplateDef[HandlebarsConfig] {
  @BeanProperty var handlebars: HandlebarsConfigDetailDef = null

  override def toValue: ValidationNel[String, Option[HandlebarsConfig]] = {
    Option(handlebars) match {
      case None => none[HandlebarsConfig].successNel[String]
      case Some(bean) => {

        val prettyPrintVal = Option(bean.prettyPrint) match {
          case None => none[Boolean].successNel[String]
          case Some(value) => value.parseBoolean.rightMap(_.some)
            .leftMap(e => s"${e.getMessage} (at 'handlebars.prettyPrintVal')").toValidationNel
        }

        val stringParamsVal = Option(bean.stringParams) match {
          case None => none[Boolean].successNel[String]
          case Some(value) => value.parseBoolean.rightMap(_.some)
            .leftMap(e => s"${e.getMessage} (at 'handlebars.stringParamsVal')").toValidationNel
        }

        val infiniteLoopsVal = Option(bean.infiniteLoops) match {
          case None => none[Boolean].successNel[String]
          case Some(value) => value.parseBoolean.rightMap(_.some)
            .leftMap(e => s"${e.getMessage} (at 'handlebars.infiniteLoopsVal')").toValidationNel
        }

        val deletePartialAfterMergeVal = Option(bean.deletePartialAfterMerge) match {
          case None => none[Boolean].successNel[String]
          case Some(value) => value.parseBoolean.rightMap(_.some)
            .leftMap(e => s"${e.getMessage} (at 'handlebars.deletePartialAfterMergeVal')").toValidationNel
        }

        (prettyPrintVal |@| stringParamsVal |@| infiniteLoopsVal |@| deletePartialAfterMergeVal) {
          HandlebarsConfig(
            Option(bean.suffix), _, _, _, _, Option(bean.startDelimiter), Option(bean.endDelimiter)
          ).some
        }
      }
    }

  }
}

class HandlebarsConfigDetailDef {
  @BeanProperty var suffix: String = null
  @BeanProperty var prettyPrint: String = null
  @BeanProperty var stringParams: String = null
  @BeanProperty var infiniteLoops: String = null
  @BeanProperty var deletePartialAfterMerge: String = null
  @BeanProperty var startDelimiter: String = null
  @BeanProperty var endDelimiter: String = null
}
