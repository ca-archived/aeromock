package jp.co.cyberagent.aeromock.template.groovytemplate

import jp.co.cyberagent.aeromock.config.definition.SpecifiedTemplateDef

import scala.beans.BeanProperty
import scalaz.Scalaz._
import scalaz._

/**
 *
 * @author stormcat24
 */
class GroovyTemplateConfigDef extends SpecifiedTemplateDef[GroovyTemplateConfig] {
  @BeanProperty var groovyTemplate: GroovyTemplateDefailDef = null

  override def toValue: ValidationNel[String, Option[GroovyTemplateConfig]] = {

    Option(groovyTemplate) match {
      case None => none[GroovyTemplateConfig].successNel[String]
      case Some(bean) => {

        // TODO validations
        val extensionVal = Option(bean.extension) match {
          case None => none[String].successNel[String]
          case Some(value) => value.some.successNel[String]
        }

        val modeVal = Option(bean.mode) match {
          case None => none[String].successNel[String]
          case Some(value) => value.some.successNel[String]
        }

        (extensionVal |@| modeVal) {
          GroovyTemplateConfig(_, _).some
        }
      }
    }
  }
}
