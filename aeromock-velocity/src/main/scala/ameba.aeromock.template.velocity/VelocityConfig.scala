package ameba.aeromock.template.velocity

import ameba.aeromock.config.TemplateConfig
import ameba.aeromock.config.definition.SpecifiedTemplateDef
import scala.beans.BeanProperty
import scalaz._
import Scalaz._

/**
 * Configuration of class for Velocity.
 * @author stormcat24
 */
case class VelocityConfig(
  extension: Option[String]) extends TemplateConfig

class VelocityConfigDef extends SpecifiedTemplateDef[VelocityConfig] {
  @BeanProperty var velocity: VelocityConfigDetailDef = null

  override def toValue: ValidationNel[String, Option[VelocityConfig]] = {

    Option(velocity) match {
      case None => none[VelocityConfig].successNel[String]
      case Some(bean) => {
        // TODO validations
        val extensionVal = Option(bean.extension) match {
          case None => none[String].successNel[String]
          case Some(value) => value.some.successNel[String]
        }

        for {
          extension <- extensionVal
        } yield (VelocityConfig(extension).some)
      }
    }

  }
}

class VelocityConfigDetailDef {
  @BeanProperty var extension: String = null
}
