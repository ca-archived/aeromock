package jp.co.cyberagent.aeromock.template

import jp.co.cyberagent.aeromock.config.definition.SpecifiedTemplateDef
import jp.co.cyberagent.aeromock.config.{ConfigHolder, TemplateConfig}
import jp.co.cyberagent.aeromock.core.annotation.TemplateIdentifier
import jp.co.cyberagent.aeromock.data.YamlDataFileReader
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.{AeromockBadUsingException, AeromockConfigurationException, AeromockResourceNotFoundException}
import org.apache.commons.lang3.reflect.ConstructorUtils

import scalaz.Scalaz._
import scalaz._

/**
 * Factory object of [[jp.co.cyberagent.aeromock.template.TemplateService]].
 * @author stormcat24
 */
object TemplateServiceFactory {

  def create(): Option[TemplateService] = {
    val project = ConfigHolder.getProject
    val userConfig = ConfigHolder.getUserConfig

    project.template match {
      case Success(Some(template)) => {
        val serviceClass = template.serviceClass

        val annotation = serviceClass.getAnnotation(classOf[TemplateIdentifier])
        if (annotation == null) {
          throw new AeromockBadUsingException("badusing.templateservice.needs.annotation", null, serviceClass.getName())
        }


        if (annotation.specialConfig()) {
          val constructor = ConstructorUtils.getAccessibleConstructor(serviceClass)
          constructor.newInstance().some

        } else {
          val templateConfigPath = userConfig.getProjectDirectory / "template.yaml"

          if (!templateConfigPath.exists) {
            throw new AeromockResourceNotFoundException(templateConfigPath.toString())
          }

          val templateLocalDef = new YamlDataFileReader()
            .deserialize(templateConfigPath, annotation.configType()).asInstanceOf[SpecifiedTemplateDef[_ <: TemplateConfig]]

          templateLocalDef.toValue match {
            case Success(Some(templateConfig)) => {
              val constructor = ConstructorUtils.getAccessibleConstructor(serviceClass, templateConfig.getClass())
              constructor.newInstance(templateConfig).some
            }
            case Success(None) => none
            case Failure(errors) => throw new AeromockConfigurationException(templateConfigPath, errors)
          }
        }
      }
      case _ => None
    }

  }

}
