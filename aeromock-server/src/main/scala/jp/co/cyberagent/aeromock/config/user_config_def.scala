package jp.co.cyberagent.aeromock.config.definition

import java.nio.file.{Paths, Path}
import java.util.Locale

import jp.co.cyberagent.aeromock.config.UserConfig
import jp.co.cyberagent.aeromock.helper._
import org.apache.commons.lang3.StringUtils


import scala.beans.BeanProperty
import scalaz.Scalaz._
import scalaz._

class UserConfigDef {

  @BeanProperty var project_config_path: String = null
  @BeanProperty var log_level: String = null
  @BeanProperty var locale: String = null
  @BeanProperty var country: String = null

  def toValue(userConfigPath: Path): ValidationNel[String, UserConfig] = {

    val projectConfigPathResult = project_config_path match {
      case null => "'project_config_path' at config.yaml not specfied.".failureNel[Path]
      case s if StringUtils.isBlank(s) => "'project_config_path' at config.yaml must be not blank".failureNel[Path]
      case _ => {
        val projectConfigPath = Paths.get(project_config_path).withHomeDirectory
        val configPath = if (projectConfigPath.isAbsolute) {
          projectConfigPath
        } else {
          userConfigPath.toAbsolutePath.getParent / project_config_path
        }
        if (!configPath.exists) {
          s"Illegal Value at 'project_config_path'. ${configPath} not exists.".failureNel[Path]
        } else if (configPath.isDirectory) {
          s"Illegal Value at 'project_config_path'. ${configPath} must be file.".failureNel[Path]
        } else {
          configPath.successNel
        }
      }
    }

    val languageResult = Option(locale) match {
      case None => none[Locale].successNel[String]
      case Some(value) => {
        val pattern = "^[a-z]{2}_[A-Z]{2}$".r
        pattern.findFirstMatchIn(value) match {
          case None => s"$value is not locale format.".failureNel[Option[Locale]]
          case Some(group) => {
            val tokens = group.matched.split("_")
            new Locale(tokens(0), tokens(1)).some.successNel
          }
        }
      }
    }
    (projectConfigPathResult |@| languageResult) apply UserConfig

  }
}
