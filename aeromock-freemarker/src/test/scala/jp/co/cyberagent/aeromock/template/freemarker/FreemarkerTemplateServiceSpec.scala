package jp.co.cyberagent.aeromock.template.freemarker

import java.nio.file.Path

import jp.co.cyberagent.aeromock.config.definition.ProjectDef
import jp.co.cyberagent.aeromock.core.http.VariableManager
import jp.co.cyberagent.aeromock.template.TemplateService
import jp.co.cyberagent.aeromock.{AeromockTestModule, SpecSupport}
import jp.co.cyberagent.aeromock.helper._
import org.specs2.mutable.{Specification, Tables}

import scala.io.Source

/**
 *
 * @author stormcat24
 */
class FreemarkerTemplateServiceSpec extends Specification with Tables with SpecSupport {

  "render" should {
    "tutorial" in {
      implicit val module = new AeromockTestModule {
        override val projectConfigPath: Path = getResourcePath(".").resolve("../../../../tutorial/freemarker/project.yaml").toRealPath()
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
