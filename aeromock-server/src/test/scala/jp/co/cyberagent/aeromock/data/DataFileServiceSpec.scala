package jp.co.cyberagent.aeromock.data

import java.nio.file.Paths

import jp.co.cyberagent.aeromock.config.definition.{DataDef, ProjectDef}
import jp.co.cyberagent.aeromock.core.http.Endpoint
import org.specs2.mutable.{Tables, Specification}

/**
 *
 * @author stormcat24
 */
class DataFileServiceSpec extends Specification with Tables {

  val projectRootPath = Paths.get(Thread.currentThread().getContextClassLoader().getResource("data/DataFileService/").getPath)

  val projectDef = new ProjectDef
  val dataDef = new DataDef
  dataDef.root = "./data"
  projectDef.data = dataDef

  val project = projectDef.toValue(projectRootPath.resolve("project.yaml"), projectRootPath)

  "getRelatedDataFiles" should {

    val service = new DataFileService(project)

    "test" in {

      val candidates = service.getRelatedDataFiles(Endpoint("/path1"))
      println(candidates)
      println(candidates.size)
      println("")
      true
    }

  }

}
