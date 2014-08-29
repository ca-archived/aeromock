package jp.co.cyberagent.aeromock.config.definition

import jp.co.cyberagent.aeromock.config.TemplateConfig

import scalaz._

trait SpecifiedTemplateDef[T <: TemplateConfig] {

  def toValue: ValidationNel[String, Option[T]]

}
