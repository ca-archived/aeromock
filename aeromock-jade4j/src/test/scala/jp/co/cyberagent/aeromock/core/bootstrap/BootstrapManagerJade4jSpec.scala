package jp.co.cyberagent.aeromock.core.bootstrap

import jp.co.cyberagent.aeromock.SpecSupport
import org.specs2.mutable.{Tables, Specification}

/**
 *
 * @author stormcat24
 */
class BootstrapManagerJade4jSpec extends Specification with Tables with SpecSupport {

  "BootstrapManager" should {
    "delegete" in {
      BootstrapManager.delegate.collectFirst {
        case (EnabledMode.JADE4j, either) => either
      } must beSome(beRight())
    }
  }
}

