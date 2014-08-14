package jp.co.cyberagent.aeromock.template.jade4j

import de.neuland.jade4j.Jade4J
import jp.co.cyberagent.aeromock.config.definition.SpecifiedTemplateDef

import scala.beans.BeanProperty
import scalaz.Scalaz._
import scalaz._

/**
 *
 * @author stormcat24
 */
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
