package jp.co.cyberagent.aeromock.template.velocity

import jp.co.cyberagent.aeromock.config.definition.SpecifiedTemplateDef

import scala.beans.BeanProperty
import scalaz.Scalaz._
import scalaz._

/**
 *
 * @author stormcat24
 */
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
