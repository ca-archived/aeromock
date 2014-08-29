package jp.co.cyberagent.aeromock.config

import java.nio.file.Path
import java.util.Locale

case class UserConfig(projectConfigPath: Path, language: Option[Locale]) {
  def projectDirectory = projectConfigPath.getParent()
}
