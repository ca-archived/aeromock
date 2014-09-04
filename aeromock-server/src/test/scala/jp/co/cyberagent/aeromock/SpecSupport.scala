package jp.co.cyberagent.aeromock

import java.nio.file.{Path, Paths}

/**
 *
 * @author stormcat24
 */
trait SpecSupport {

  def getResourcePath(path: String): Path = {
    val url = Thread.currentThread().getContextClassLoader.getResource(path)
    Paths.get(url.getPath)
  }
}
