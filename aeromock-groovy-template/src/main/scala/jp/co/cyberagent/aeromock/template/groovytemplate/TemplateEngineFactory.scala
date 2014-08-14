package jp.co.cyberagent.aeromock.template.groovytemplate

import groovy.text.{GStringTemplateEngine, SimpleTemplateEngine, TemplateEngine, XmlTemplateEngine}

/**
 * Factory object to create [[groovy.text.TemplateEngine]].
 * @author stormcat24
 */
object TemplateEngineFactory {

  /**
   * Create [[groovy.text.TemplateEngine]].
   * @param modeType mode of template engine
   * @return [[groovy.text.TemplateEngine]]
   */
  def create(modeType: Option[String]): TemplateEngine = {
    modeType match {
      case None => new SimpleTemplateEngine()
      case Some("simple") => new SimpleTemplateEngine()
      case Some("gstring") => new GStringTemplateEngine()
      case Some("xml") => new XmlTemplateEngine()
    }
  }

}
