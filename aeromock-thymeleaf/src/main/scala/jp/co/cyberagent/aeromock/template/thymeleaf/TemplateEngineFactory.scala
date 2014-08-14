package jp.co.cyberagent.aeromock.template.thymeleaf

import java.nio.file.Path

import org.thymeleaf.TemplateEngine
import org.thymeleaf.templateresolver.FileTemplateResolver
import scala.collection.JavaConverters._

/**
 * Factory object to create [[org.thymeleaf.TemplateEngine]]
 * @author stormcat24
 */
object TemplateEngineFactory {

  /**
   * Create [[org.thymeleaf.TemplateEngine]]
   * @param templateRoot template root
   * @param functionRoot function root
   * @param config configuration of thymeleaf
   * @return [[org.thymeleaf.TemplateEngine]]
   */
  def create(templateRoot: Path, functionRoot: Path, config: ThymeleafConfig): TemplateEngine = {
    val resolver = new FileTemplateResolver
    resolver.setPrefix(templateRoot.toAbsolutePath.toString)
    resolver.setCacheable(false)

    config.suffix.map(v => resolver.setSuffix(v))
    config.characterEncoding.map(v => resolver.setCharacterEncoding(v))
    config.templateMode.map(v => resolver.setTemplateMode(v))
    config.legacyHtml5TemplateModePatterns.map(v => resolver.setLegacyHtml5TemplateModePatterns(v.toSet.asJava))
    config.validXhtmlTemplateModePatterns.map(v => resolver.setValidXhtmlTemplateModePatterns(v.toSet.asJava))
    config.validXmlTemplateModePatterns.map(v => resolver.setValidXmlTemplateModePatterns(v.toSet.asJava))
    config.xhtmlTemplateModePatterns.map(v => resolver.setXhtmlTemplateModePatterns(v.toSet.asJava))
    config.xmlTemplateModePatterns.map(v => resolver.setXmlTemplateModePatterns(v.toSet.asJava))

    val helperDialect = new DynamicUtilityObject(functionRoot)

    val engine = new TemplateEngine
    engine.setAdditionalDialects(new java.util.HashSet(List(helperDialect).asJava))
    engine.setTemplateResolver(resolver)
    engine
  }
}
