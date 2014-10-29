package jp.co.cyberagent.aeromock.template.jade4j

import jp.co.cyberagent.aeromock.test.SpecSupport
import jp.co.cyberagent.aeromock.helper._
import org.specs2.mutable.{Tables, Specification}

/**
 *
 * @author stormcat24
 */
class Jade4jBootstrapSpec extends Specification with Tables with SpecSupport {

  "Jade4jBootstrap" should {
    "process" in {
      trye(new Jade4jBootstrap().process) must beRight()
    }
  }
}
