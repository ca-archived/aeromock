package ameba.aeromock.template.jade4j

import ameba.aeromock.AeromockConfigurationException
import ameba.aeromock.config.entity.Project
import ameba.aeromock.core.script.GroovyScriptRunner
import ameba.aeromock.helper._
import de.neuland.jade4j.JadeConfiguration
import de.neuland.jade4j.template.FileTemplateLoader
import groovy.lang.{Binding, GroovyShell}

import scala.collection.JavaConverters._
import scalaz.Scalaz._
import scalaz._

/**
 * Factort to create [[de.neuland.jade4j.JadeConfiguration]].
 * @author stormcat24
 */
object JadeConfigurationFactory {

  /**
   * Create [[de.neuland.jade4j.JadeConfiguration]].
   * @param config [[Jade4jConfig]]
   * @return [[de.neuland.jade4j.JadeConfiguration]]
   */
  def create(project: Project, config: Jade4jConfig): JadeConfiguration = {

    val configuration = new JadeConfiguration

    // directory path must finish slash.
    val templateLoader = new FileTemplateLoader(project._template.root.toString + "/", "UTF-8")
    configuration.setTemplateLoader(templateLoader)

    config.mode.map(v => configuration.setMode(v))
    config.prettyPrint.map(v => configuration.setPrettyPrint(v))

    val functionRootDir = project.function match {
      case Success(Some(value)) => value.root.some
      case Failure(errors) => throw new AeromockConfigurationException(project.projectConfig, errors)
      case _ => None
    }

    functionRootDir match {
      case None =>
      case Some(functionRoot) => {
        val functionMap = (functionRoot.getChildren().map { script =>
          val scriptName = script.getFileName().toString()
          val shell = new GroovyShell()
          (scriptName.replace(".groovy", "") -> shell.run(script.toAbsolutePath.toFile(), Array.empty[String]))
        }).toMap[String, AnyRef]

        configuration.setSharedVariables(functionMap.asJava)
      }
    }

    // hook script
    if (project.templateScript.exists()) {
      val binding = new Binding()
      binding.setProperty("configuration", configuration)
      new GroovyScriptRunner[Unit](project.templateScript).run(binding)
    }

    configuration
  }
}
