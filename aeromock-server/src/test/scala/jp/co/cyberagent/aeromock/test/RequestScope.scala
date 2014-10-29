package jp.co.cyberagent.aeromock.test

import jp.co.cyberagent.aeromock.core.http.VariableManager
import org.specs2.specification.{After, Before, Scope}

/**
 *
 * @author stormcat24
 */
object RequestScope extends Scope with Before {

  override def before = {
    VariableManager.initializeRequestMap(Map(
      "USER_AGENT" -> "test",
      "REQUEST_URI" -> "/test",
      "HOST" -> "localhost:3183",
      "QUERY_STRING" -> "",
      "REMOTE_HOST" -> "localhost"
    ))
    VariableManager.initializeOriginalVariableMap(new java.util.HashMap[String, AnyRef]())
  }

}
