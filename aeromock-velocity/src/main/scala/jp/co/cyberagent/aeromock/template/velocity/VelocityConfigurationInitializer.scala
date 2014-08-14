package jp.co.cyberagent.aeromock.template.velocity

import java.io.FileInputStream
import java.nio.file.Files

import jp.co.cyberagent.aeromock.config.entity.Project
import jp.co.cyberagent.aeromock.util.ResourceUtil
import jp.co.cyberagent.aeromock.helper._
import org.apache.commons.collections.ExtendedProperties
import org.apache.velocity.app.Velocity

/**
 * Initializer velocity configuration.
 * @author stromcat24
 */
object VelocityConfigurationInitializer {

  /**
   * do initialization
   * @param project [[jp.co.cyberagent.aeromock.config.entity.Project]]
   */
  def initialize(project: Project) {

    Velocity.setProperty("velocimacro.library.autoreload", "true")
    Velocity.setProperty("file.resource.loader.path", project._template.root.toString())

    val propertiesPath = project.root / "velocity.properties"
    if (Files.exists(propertiesPath)) {
      val properties = new ExtendedProperties()
      val is = new FileInputStream(propertiesPath.toAbsolutePath.toFile())
      ResourceUtil.processResrouce(is) { is =>
        properties.load(is)
      }
      Velocity.setExtendedProperties(properties)
    }
    Velocity.init()

  }
}
