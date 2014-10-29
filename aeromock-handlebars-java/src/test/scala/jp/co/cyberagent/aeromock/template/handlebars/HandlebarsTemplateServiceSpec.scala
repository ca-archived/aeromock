package jp.co.cyberagent.aeromock.template.handlebars

import java.nio.file.Path

import jp.co.cyberagent.aeromock.config.definition.ProjectDef
import jp.co.cyberagent.aeromock.core.http.VariableManager
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.template.TemplateService
import jp.co.cyberagent.aeromock.AeromockTestModule
import jp.co.cyberagent.aeromock.test.SpecSupport
import org.specs2.mutable.{Tables, Specification}

/**
 *
 * @author stormcat24
 */
class HandlebarsTemplateServiceSpec extends Specification with Tables with SpecSupport {

  "render" should {
    "tutorial" in {
      implicit val module = new AeromockTestModule {
        override val projectConfigPath: Path = getResourcePath(".").resolve("../../../../tutorial/handlebars/project.yaml").toRealPath()
        override val projectDefArround = (projectDef: ProjectDef) => {}
      }

      val service = inject[Option[TemplateService]].get
      VariableManager.initializeRequestMap(Map(
        "USER_AGENT" -> "test",
        "REQUEST_URI" -> "/test",
        "HOST" -> "localhost:3183",
        "QUERY_STRING" -> "",
        "REMOTE_HOST" -> "localhost"
      ))
      VariableManager.initializeOriginalVariableMap(new java.util.HashMap[String, AnyRef]())

      trye(service.render(request("/test"))).isRight
    }
  }
}
