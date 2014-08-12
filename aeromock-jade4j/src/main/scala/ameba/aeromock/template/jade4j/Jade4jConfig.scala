package ameba.aeromock.template.jade4j

import ameba.aeromock.config.TemplateConfig
import ameba.aeromock.config.definition.SpecifiedTemplateDef
import de.neuland.jade4j.Jade4J

import scala.beans.BeanProperty
import scalaz.Scalaz._
import scalaz._

/**
 * Configuration of class for jade4j.
 * @author stormcat24
 */
case class Jade4jConfig(
  extension: Option[String],
  mode: Option[Jade4J.Mode],
  prettyPrint: Option[Boolean]
) extends TemplateConfig


class Jade4jConfigDef extends SpecifiedTemplateDef[Jade4jConfig] {
  @BeanProperty var jade4j: Jade4jConfigDetailDef = null

  override def toValue: ValidationNel[String, Option[Jade4jConfig]] = {
    Option(jade4j) match {
      case None => none[Jade4jConfig].successNel[String]
      case Some(value) => {

        val extensionVal = Option(value.extension) match {
          case None => none[String].successNel[String]
          case Some(extension) => extension.some.successNel
        }

        val modeVal = Option(value.mode) match {
          case None => none[Jade4J.Mode].successNel[String]
          case Some(value) => Jade4J.Mode.valueOf(value).some.successNel
        }

        val prettyPrintVal = Option(value.prettyPrint) match {
          case None => none[Boolean].successNel[String]
          case Some(value) => value.parseBoolean.rightMap(_.some).leftMap(e => s"${e.getMessage} (at 'jade4j.prettyPrint')").toValidationNel
        }

        (extensionVal |@| modeVal |@| prettyPrintVal) {Jade4jConfig(_, _, _).some}
      }
    }
  }
}

class Jade4jConfigDetailDef {

  @BeanProperty var extension: String = null
  @BeanProperty var mode: String = null
  @BeanProperty var prettyPrint: String = null

}
