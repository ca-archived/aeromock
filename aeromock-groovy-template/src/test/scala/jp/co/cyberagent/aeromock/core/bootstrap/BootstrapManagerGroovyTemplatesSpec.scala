package jp.co.cyberagent.aeromock.core.bootstrap

import jp.co.cyberagent.aeromock.SpecSupport
import org.specs2.mutable.{Tables, Specification}

/**
 *
 * @author stormcat24
 */
class BootstrapManagerGroovyTemplatesSpec extends Specification with Tables with SpecSupport {

  "BootstrapManager" should {
    "delegete" in {
      BootstrapManager.delegate.collectFirst {
        case (EnabledMode.GROOVY_TEMPLATE, either) => either
      } must beSome(beRight())
    }
  }

}
