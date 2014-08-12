package ameba.aeromock.template.freemarker

import ameba.aeromock.config.TemplateConfig
import ameba.aeromock.config.definition.SpecifiedTemplateDef
import ameba.aeromock.template.freemarker.ext.Struts2BeanWrapper
import freemarker.core.ArithmeticEngine
import freemarker.template.ObjectWrapper

import scala.beans.BeanProperty
import scala.collection.JavaConverters._
import scalaz.Scalaz._
import scalaz._

/**
 * Configuration of class for Freemarker.
 * @author stormcat24
 */
class FreemarkerConfig(
  val extension: String,
  val autoEscape: Option[Boolean],
  val objectWrapper: ObjectWrapper,
  val autoFlush: Option[Boolean],
  val autoIncludes: List[String],
  val booleanFormat: Option[String],
  val classicCompatible: Option[Boolean],
  val classicCompatibleAsInt: Option[Int],
  val dateFormat: Option[String],
  val dateTimeFormat: Option[String],
  val defaultEncoding: Option[String],
  val localizedLookup: Option[Boolean],
  val numberFormat: Option[String],
  val outputEncoding: Option[String],
  val strictBeanModels: Option[Boolean],
  val strictSyntaxMode: Option[Boolean],
  val tagSyntax: Option[Int],
  val templateUpdateDelay: Option[Int],
  val urlEscapingCharset: Option[String],
  val whitespaceStripping: Option[Boolean],
  val arithmeticEngine: Option[ArithmeticEngine],
  val autoImports: Map[String, String]) extends TemplateConfig

object FreemarkerConfig {
  def apply(bean: FreemarkerConfigDetailDef): FreemarkerConfig = {

    val objectWrapper = Option(bean.objectWrapper) match {
      case Some("BEANS_WRAPPER") => ObjectWrapper.BEANS_WRAPPER
      case Some("STRUTS2_BEANS_WRAPPER") => new Struts2BeanWrapper(true)
      case Some("SIMPLE_WRAPPER") => ObjectWrapper.SIMPLE_WRAPPER
      case _ => ObjectWrapper.DEFAULT_WRAPPER
    }

    val arithmeticEngine = Option(bean.arithmeticEngine) match {
      case Some("BIGDECIMAL_ENGINE") => ArithmeticEngine.BIGDECIMAL_ENGINE.some
      case Some("CONSERVATIVE_ENGINE") => ArithmeticEngine.CONSERVATIVE_ENGINE.some
      case _ => none
    }

    new FreemarkerConfig(
      extension = bean.extension,
      autoEscape = Option(bean.autoEscape).flatMap(_.parseBoolean.toOption),
      objectWrapper = objectWrapper,
      autoFlush = Option(bean.autoFlush).flatMap(_.parseBoolean.toOption),
      autoIncludes = bean.autoIncludes.asScala.toList,
      booleanFormat = Option(bean.booleanFormat),
      classicCompatible = Option(bean.classicCompatible).flatMap(_.parseBoolean.toOption),
      classicCompatibleAsInt = Option(bean.classicCompatibleAsInt).flatMap(_.parseInt.toOption),
      dateFormat = Option(bean.dateFormat),
      dateTimeFormat = Option(bean.dateTimeFormat),
      defaultEncoding = Option(bean.defaultEncoding),
      localizedLookup = Option(bean.localizedLookup).flatMap(_.parseBoolean.toOption),
      numberFormat = Option(bean.numberFormat),
      outputEncoding = Option(bean.outputEncoding),
      strictBeanModels = Option(bean.strictBeanModels).flatMap(_.parseBoolean.toOption),
      strictSyntaxMode = Option(bean.strictSyntaxMode).flatMap(_.parseBoolean.toOption),
      tagSyntax = Option(bean.tagSyntax).flatMap(_.parseInt.toOption),
      templateUpdateDelay = Option(bean.templateUpdateDelay).flatMap(_.parseInt.toOption),
      urlEscapingCharset = Option(bean.urlEscapingCharset),
      whitespaceStripping = Option(bean.whitespaceStripping).flatMap(_.parseBoolean.toOption),
      arithmeticEngine = arithmeticEngine,
      autoImports = bean.autoImports.asScala.toMap
    )
  }
}

class FreemarkerConfigDef extends SpecifiedTemplateDef[FreemarkerConfig] {
  @BeanProperty var freemarker: FreemarkerConfigDetailDef = null

  override def toValue: ValidationNel[String, Option[FreemarkerConfig]] = {
    Option(freemarker) match {
      case None => none[FreemarkerConfig].successNel[String]
      case Some(bean) => FreemarkerConfig(bean).some.successNel[String]
    }
  }
}

class FreemarkerConfigDetailDef {
  @BeanProperty var extension: String = ".ftl"
  @BeanProperty var autoEscape: String = null
  @BeanProperty var objectWrapper: String = null
  @BeanProperty var autoFlush: String = null
  @BeanProperty var autoIncludes: java.util.List[String] = new java.util.ArrayList[String]
  @BeanProperty var booleanFormat: String = null
  @BeanProperty var classicCompatible: String = null
  @BeanProperty var classicCompatibleAsInt: String = null
  @BeanProperty var dateFormat: String = null
  @BeanProperty var dateTimeFormat: String = null
  @BeanProperty var defaultEncoding: String = null
  @BeanProperty var localizedLookup: String = null
  @BeanProperty var numberFormat: String = null
  @BeanProperty var outputEncoding: String = null
  @BeanProperty var strictBeanModels: String = null
  @BeanProperty var strictSyntaxMode: String = null
  @BeanProperty var tagSyntax: String = null
  @BeanProperty var templateUpdateDelay: String = null
  @BeanProperty var urlEscapingCharset: String = null
  @BeanProperty var whitespaceStripping: String = null
  @BeanProperty var arithmeticEngine: String = null
  @BeanProperty var autoImports: java.util.Map[String, String] = new java.util.HashMap[String, String]
}
