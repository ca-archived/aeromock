package ameba.aeromock.config.definition

import ameba.aeromock.config.TemplateConfig

import scalaz.ValidationNel

abstract class SpecifiedTemplateDef[T <: TemplateConfig] {

  def toValue: ValidationNel[String, Option[T]]

}