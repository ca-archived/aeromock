package jp.co.cyberagent.aeromock.template.handlebars

import jp.co.cyberagent.aeromock.SpecSupport
import jp.co.cyberagent.aeromock.helper._
import org.specs2.mutable.{Specification, Tables}

/**
 *
 * @author stormcat24
 */
class HandlebarsBootstrapSpec extends Specification with Tables with SpecSupport {

  "HandlebarsBootstrap" should {
    "process" in {
      trye(new HandlebarsBootstrap().process) must beRight()
    }
  }
}
