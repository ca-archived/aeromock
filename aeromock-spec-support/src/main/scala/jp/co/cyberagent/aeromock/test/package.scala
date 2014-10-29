package jp.co.cyberagent.aeromock

import java.nio.file.{Path, Paths}

import io.netty.handler.codec.http.{DefaultFullHttpRequest, FullHttpRequest, HttpMethod, HttpVersion}
import scaldi.Injectable


/**
 *
 * @author stormcat24
 */
package object test {

  trait SpecSupport extends AnyRef with Injectable {
    def getResourcePath(path: String): Path = {
      val url = Thread.currentThread().getContextClassLoader.getResource(path)
      Paths.get(url.getPath)
    }

    def request(uri: String, method: HttpMethod = HttpMethod.GET): FullHttpRequest = {
      new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, method, uri)
    }
  }

}
