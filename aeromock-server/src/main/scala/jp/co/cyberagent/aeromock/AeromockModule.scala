package jp.co.cyberagent.aeromock

import java.nio.file.{Path, Paths}

import jp.co.cyberagent.aeromock.api.controller.DataCreateController
import jp.co.cyberagent.aeromock.config._
import jp.co.cyberagent.aeromock.config.definition.{SpecifiedTemplateDef, UserConfigDef}
import jp.co.cyberagent.aeromock.core.annotation.TemplateIdentifier
import jp.co.cyberagent.aeromock.core.http.HttpRequestProcessor
import jp.co.cyberagent.aeromock.core.{CacheKey, ObjectCache}
import jp.co.cyberagent.aeromock.data.{JsonDataFileReader, YamlDataFileReader}
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.server.http._
import jp.co.cyberagent.aeromock.template.TemplateService
import org.apache.commons.lang3.reflect.ConstructorUtils
import scaldi.{Injector, Module}

import scalaz.Scalaz._
import scalaz.{Failure, Success}

/**
 *
 * @author stormcat24
 */
trait AeromockModule extends Module {

  val serverOption: ServerOption
  def createUserConfig: UserConfig
  def createProject: Project

  binding identifiedBy 'configFile to Option(serverOption).flatMap(_.configFile) | Paths.get("~/.aeromock/config.yaml").withHomeDirectory
  binding identifiedBy 'listenPort to Option(serverOption).flatMap(_.port) | AeromockInfo.defaultListenPort

  bind [JsonDataFileReader] to new JsonDataFileReader
  bind [YamlDataFileReader] to new YamlDataFileReader

  bind [UserConfig] toProvider createUserConfig
  bind [Project] toProvider createProject

  bind [Option[TemplateService]] toProvider {
    val project = inject[Project]
    val userConfig = inject[UserConfig]
    project.template match {
      case Success(Some(template)) => {
        val serviceClass = template.serviceClass

        val annotation = serviceClass.getAnnotation(classOf[TemplateIdentifier])
        if (annotation == null) {
          throw new AeromockBadUsingException("badusing.templateservice.needs.annotation", null, serviceClass.getName())
        }

        if (annotation.specialConfig()) {
          val constructor = ConstructorUtils.getAccessibleConstructor(serviceClass, classOf[Injector])
          constructor.newInstance(this).some

        } else {
          val templateConfigPath = userConfig.projectConfigPath.getParent / "template.yaml"

          if (!templateConfigPath.exists) {
            throw new AeromockResourceNotFoundException(templateConfigPath.toString())
          }

          val templateLocalDef = new YamlDataFileReader()
            .deserialize(templateConfigPath, annotation.configType()).asInstanceOf[SpecifiedTemplateDef[_ <: TemplateConfig]]

          templateLocalDef.toValue match {
            case Success(Some(templateConfig)) => {
              val constructor = ConstructorUtils.getAccessibleConstructor(serviceClass, templateConfig.getClass(), classOf[Injector])
              constructor.newInstance(templateConfig, this).some
            }
            case Success(None) => none
            case Failure(errors) => throw new AeromockConfigurationException(templateConfigPath, errors)
          }
        }
      }
      case _ => None
    }
  }

  //  bind [TemplateServiceFactory] to new TemplateServiceFactory
  bind [HttpRequestProcessor] to new HttpRequestProcessor
  bind [DataCreateController] toProvider new DataCreateController

  // HttpRequestProcessor
  bind [AeromockStaticFileHttpRequestProcessor] toProvider new AeromockStaticFileHttpRequestProcessor
  bind [AeromockApiHttpRequestProcessor] toProvider new AeromockApiHttpRequestProcessor
  bind [TemplateHttpRequestProcessor] toProvider new TemplateHttpRequestProcessor
  bind [JsonApiHttpRequestProcessor] toProvider new JsonApiHttpRequestProcessor
  bind [UserStaticFileHttpRequestProcessor] toProvider new UserStaticFileHttpRequestProcessor
  bind [ProtobufResponseWriter] toProvider new ProtobufResponseWriter
}
