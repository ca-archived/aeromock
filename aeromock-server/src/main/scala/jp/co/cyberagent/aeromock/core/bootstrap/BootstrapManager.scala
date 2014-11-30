package jp.co.cyberagent.aeromock.core.bootstrap

import org.apache.commons.lang3.ClassUtils
import org.slf4j.LoggerFactory
import jp.co.cyberagent.aeromock.helper._

/**
 * manager to control bootstrap.
 * @author stormcat24
 */
object BootstrapManager {

  val LOG = LoggerFactory.getLogger(this.getClass())

  def delegate = {
    EnabledMode.values.map { mode =>
      (mode, trye {
        val bootstrapClass = ClassUtils.getClass(mode.fqdn).asInstanceOf[Class[_ <: Bootstrap]]

        if (bootstrapClass != null) {
          bootstrapClass.newInstance.process
          LOG.info(s"## Prepared ${mode} module.")
        }
      })
    }
  }

}

sealed abstract class EnabledMode(val fqdn: String)

object EnabledMode {

  case object FREEMARKER extends EnabledMode("jp.co.cyberagent.aeromock.template.freemarker.FreemarkerBootstrap")

  case object HANDLEBARS extends EnabledMode("jp.co.cyberagent.aeromock.template.handlebars.HandlebarsBootstrap")

  case object JADE4j extends EnabledMode("jp.co.cyberagent.aeromock.template.jade4j.Jade4jBootstrap")

  case object VELOCITY extends EnabledMode("jp.co.cyberagent.aeromock.template.velocity.VelocityBootstrap")

  case object GROOVY_TEMPLATE extends EnabledMode("jp.co.cyberagent.aeromock.template.groovytemplate.GroovyTemplateBootstrap")

  case object THYMELEAF extends EnabledMode("jp.co.cyberagent.aeromock.template.thymeleaf.ThymeleafBootstrap")

  val values = Array[EnabledMode](
    FREEMARKER,
    HANDLEBARS,
    JADE4j,
    VELOCITY,
    GROOVY_TEMPLATE,
    THYMELEAF
  )
}
