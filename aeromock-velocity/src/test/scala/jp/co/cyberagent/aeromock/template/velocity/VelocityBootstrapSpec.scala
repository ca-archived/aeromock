package jp.co.cyberagent.aeromock.template.velocity

import jp.co.cyberagent.aeromock.test.SpecSupport
import jp.co.cyberagent.aeromock.helper._
import org.specs2.mutable.{Specification, Tables}

/**
 *
 * @author stormcat24
 */
class VelocityBootstrapSpec extends Specification with Tables with SpecSupport {

  "VelocityBootstrap" should {
    "process" in {
      trye(new VelocityBootstrap().process)
      // TODO to be right
      true
    }
  }
}
