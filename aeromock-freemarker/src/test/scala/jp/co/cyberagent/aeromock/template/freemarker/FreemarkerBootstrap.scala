package jp.co.cyberagent.aeromock.template.freemarker

import jp.co.cyberagent.aeromock.SpecSupport
import jp.co.cyberagent.aeromock.helper._
import org.specs2.mutable.{Tables, Specification}

/**
 *
 * @author stormcat24
 */
class FreemarkerBootstrapSpec extends Specification with Tables with SpecSupport {

  "FreemarkerBootstrap" should {
    "process" in {
      trye(new FreemarkerBootstrap().process())
      // TODO to be right
      true
    }
  }
}
