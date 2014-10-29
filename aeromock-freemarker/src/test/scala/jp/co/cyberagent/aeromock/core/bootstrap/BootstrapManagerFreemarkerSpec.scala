package jp.co.cyberagent.aeromock.core

import jp.co.cyberagent.aeromock.test.SpecSupport
import jp.co.cyberagent.aeromock.core.bootstrap.{BootstrapManager, EnabledMode}
import org.specs2.mutable.{Specification, Tables}

/**
 *
 * @author stormcat24
 */
class BootstrapManagerFreemarkerSpec extends Specification with Tables with SpecSupport {

  "BootstrapManager" should {
    "delegete" in {
      BootstrapManager.delegate.collectFirst {
        case (EnabledMode.FREEMARKER, either) => either
      } must beSome
      // TODO to be right
    }
  }
}
