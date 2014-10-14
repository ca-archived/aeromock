package jp.co.cyberagent.aeromock.template.thymeleaf

import jp.co.cyberagent.aeromock.SpecSupport
import jp.co.cyberagent.aeromock.helper._
import org.specs2.mutable.{Tables, Specification}

/**
 *
 * @author stormcat24
 */
class ThymeleafBootstrapSpec extends Specification with Tables with SpecSupport {

  "ThymeleafBootstrap" should {
    "process" in {
      trye(new ThymeleafBootstrap().process) must beRight()
    }
  }
}
