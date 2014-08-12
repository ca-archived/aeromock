package ameba.aeromock.template.groovytemplate

import ameba.aeromock.config.TemplateConfig
import ameba.aeromock.config.definition.SpecifiedTemplateDef

import scala.beans.BeanProperty
import scalaz._
import Scalaz._

/**
 * Configuration of class for groovy template.
 * @author stormcat24
 */
case class GroovyTemplateConfig(
  extension: Option[String],
  mode: Option[String]
) extends TemplateConfig


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

class GroovyTemplateDefailDef {
  @BeanProperty var extension: String = null
  @BeanProperty var mode: String = null
}
