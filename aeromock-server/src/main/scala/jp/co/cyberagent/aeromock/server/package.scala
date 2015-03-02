package jp.co.cyberagent.aeromock

import java.nio.file.Path

import io.netty.handler.codec.http.HttpMethod

/**
 *
 * @author stormcat24
 */
package object server {

  case class DataFile(id: Option[String], path: Path, method: HttpMethod = HttpMethod.GET)

}
