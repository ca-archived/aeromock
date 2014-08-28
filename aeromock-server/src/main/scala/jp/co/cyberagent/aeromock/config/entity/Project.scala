package jp.co.cyberagent.aeromock.config.entity

import java.nio.file.Path
import jp.co.cyberagent.aeromock.AeromockConfigurationException
import jp.co.cyberagent.aeromock.config.TemplateConfig
import jp.co.cyberagent.aeromock.template.TemplateService
import jp.co.cyberagent.aeromock.helper._

import scala.language.existentials
import scalaz._

case class Project(
  projectConfig: Path,
  root: Path,
  template: ValidationNel[String, Option[Template]],
  data: ValidationNel[String, Option[Data]],
  static: ValidationNel[String, Option[Static]],
  ajax: ValidationNel[String, Option[Ajax]],
  tag: ValidationNel[String, Option[Tag]],
  function: ValidationNel[String, Option[Function]],
  naming: ValidationNel[String, Naming],
  test: ValidationNel[String, Test]) {

  def _template: Template = {
    template match {
      case Failure(errors) => throw new AeromockConfigurationException(projectConfig, errors)
      case Success(None) => throw new AeromockConfigurationException(projectConfig, message"configuration.not.specified${"template"}")
      case Success(Some(value)) => value
    }
  }

  def _data: Data = {
    data match {
      case Failure(errors) => throw new AeromockConfigurationException(projectConfig, errors)
      case Success(None) => throw new AeromockConfigurationException(projectConfig, message"configuration.not.specified${"data"}")
      case Success(Some(value)) => value
    }
  }

  def _static: Static = {
    static match {
      case Failure(errors) => throw new AeromockConfigurationException(projectConfig, errors)
      case Success(None) => throw new AeromockConfigurationException(projectConfig, message"configuration.not.specified${"static"}")
      case Success(Some(value)) => value
    }
  }

  def _ajax: Ajax = {
    ajax match {
      case Failure(errors) => throw new AeromockConfigurationException(projectConfig, errors)
      case Success(None) => throw new AeromockConfigurationException(projectConfig, message"configuration.not.specified${"ajax"}")
      case Success(Some(value)) => value
    }
  }

  def _tag: Tag = {
    tag match {
      case Failure(errors) => throw new AeromockConfigurationException(projectConfig, errors)
      case Success(None) => throw new AeromockConfigurationException(projectConfig, message"configuration.not.specified${"tag"}")
      case Success(Some(value)) => value
    }
  }

  def _function: Function = {
    function match {
      case Failure(errors) => throw new AeromockConfigurationException(projectConfig, errors)
      case Success(None) => throw new AeromockConfigurationException(projectConfig, message"configuration.not.specified${"function"}")
      case Success(Some(value)) => value
    }
  }

  def _naming: Naming = {

    naming match {
      case Failure(value) => throw new AeromockConfigurationException(projectConfig, value)
      case Success(value) => value
    }
  }

  def _test: Test = {

    test match {
      case Failure(value) => throw new AeromockConfigurationException(projectConfig, value)
      case Success(value) => value
    }
  }

  def templateScript: Path = root / "template.script"
  def dataScript: Path = root / "data.groovy"
  def ajaxScript: Path = root / "ajax.groovy"
  def routingScript: Path = root / "routing.groovy"
  def variableScript: Path = root / "variable.groovy"
}

case class Template(
  root: Path,
  serviceClass: Class[_ <: TemplateService],
  contexts: List[TemplateContext])
case class TemplateContext(domain: String, port: Int, root: Path)
case class Data(root: Path)
case class Static(root: Path)
case class Ajax(root: Path)
case class Tag(root: Path)
case class Function(root: Path)
case class Naming(dataPrefix: String = "__", dataidQuery: String = "_dataid") {
  def methods = s"${dataPrefix}methods"
  def list = s"${dataPrefix}list"
  def json = s"${dataPrefix}json"
  def additional = s"${dataPrefix}additional"
  def debug = s"${dataPrefix}debug"
  def response = s"${dataPrefix}response"
}
case class Test(reportRoot: Path)
