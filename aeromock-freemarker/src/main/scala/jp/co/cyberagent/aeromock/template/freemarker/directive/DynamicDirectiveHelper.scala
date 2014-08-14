/**
 *
 */
package jp.co.cyberagent.aeromock.template.freemarker.directive

import java.nio.file.Path

import jp.co.cyberagent.aeromock.core.script.GroovyDirectiveScriptRunner
import jp.co.cyberagent.aeromock.helper._
import freemarker.template.TemplateModel
import jp.co.cyberagent.aeromock.template.freemarker.FreemarkerConfig

/**
 * Helper object of dynamic directive.
 * @author stormcat24
 *
 */
object DynamicDirectiveHelper {

  def getDynamicDirectives(templateScript: Path, tagRootDir: Option[Path],
    config: FreemarkerConfig): List[AeromockCustomDirective] = {

    tagRootDir match {
      case None => List.empty
      case Some(dir) if !dir.exists() => List.empty
      case Some(dir) => {
        val tagFiles = dir.getChildren().filter(p => p.toString().endsWith(config.extension) || p.toString().endsWith(".groovy"))
        val runner = new GroovyDirectiveScriptRunner(dir)
        val simpleDirectives = tagFiles.map(script => new AeromockScriptDirective(runner, script.getFileName().withoutExtension.toString))

        // ex.) s.text.groovy
        val nestedDirectives = tagFiles
          .filter(_.getFileName().withoutExtension.toString.contains(".")).map { script =>
            val scriptName = script.getFileName().withoutExtension.toString
            val tokens = scriptName.split("\\.")
            val name = tokens(1)
            val directive = new AeromockScriptDirective(runner, scriptName)
            NestedDirective(tokens(0), name, directive)
          }
          .groupBy(_.prefix)
          .map(d => {
            val nestedModel = new AeromockNestedTemplateModel(d._1)
            d._2.foreach(child => {
              nestedModel.put(child.name, child.directive)
            })
            nestedModel.asInstanceOf[AeromockCustomDirective]
          }).toList

        simpleDirectives ++ nestedDirectives
      }
    }
  }
}

case class NestedDirective(prefix: String, name: String, directive: TemplateModel)
