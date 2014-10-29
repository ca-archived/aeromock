package jp.co.cyberagent.aeromock.core.bootstrap

import jp.co.cyberagent.aeromock.test.SpecSupport
import org.specs2.mutable.{Tables, Specification}

/**
 *
 * @author stormcat24
 */
class BootstrapManagerHandlebarsSpec extends Specification with Tables with SpecSupport {

  "BootstrapManager" should {
    "delegete" in {
      BootstrapManager.delegate.collectFirst {
        case (EnabledMode.HANDLEBARS, either) => either
      } must beSome(beRight())
    }
  }
}
