package jp.co.cyberagent.aeromock.template.groovytemplate

import jp.co.cyberagent.aeromock.test.SpecSupport
import jp.co.cyberagent.aeromock.helper._
import org.specs2.mutable.{Tables, Specification}

/**
 *
 * @author stormcat24
 */
class GroovyTemplateBootstrapSpec extends Specification with Tables with SpecSupport {

  "GroovyTemplateBootstrap" should {
    "process" in {
      trye(new GroovyTemplateBootstrap().process) must beRight()
    }
  }
}
