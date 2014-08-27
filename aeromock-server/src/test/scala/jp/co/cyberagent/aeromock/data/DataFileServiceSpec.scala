package jp.co.cyberagent.aeromock.data

import java.nio.file.Paths

import io.netty.handler.codec.http.HttpMethod
import jp.co.cyberagent.aeromock.config.definition.{DataDef, ProjectDef}
import jp.co.cyberagent.aeromock.core.http.Endpoint
import jp.co.cyberagent.aeromock.data.DataFile
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

    "1st hierarchy" in {

      val actual = service.getRelatedDataFiles(Endpoint("/path1"))
      actual must contain(allOf(
        DataFile(None, project._data.root.resolve("path1.yaml")),
        DataFile(Some("2"), project._data.root.resolve("path1__2.yaml")),
        DataFile(Some("4"), project._data.root.resolve("path1__4.json")),
        DataFile(Some("xx"), project._data.root.resolve("path1__xx.yaml")),
        DataFile(None, project._data.root.resolve("path1__post.yaml"), HttpMethod.POST),
        DataFile(None, project._data.root.resolve("path1__put.yaml"), HttpMethod.PUT),
        DataFile(None, project._data.root.resolve("path1__delete.yaml"), HttpMethod.DELETE),
        DataFile(Some("2"), project._data.root.resolve("path1__post__2.yaml"), HttpMethod.POST)
      ))
    }

    "2nd hierarchy" in {
      val actual = service.getRelatedDataFiles(Endpoint("/path1/path2"))

      actual must contain(allOf(
        DataFile(None, project._data.root.resolve("path1/path2.yaml")),
        DataFile(Some("2"), project._data.root.resolve("path1/path2__2.yaml")),
        DataFile(Some("4"), project._data.root.resolve("path1/path2__4.json")),
        DataFile(Some("xx"), project._data.root.resolve("path1/path2__xx.yaml")),
        DataFile(None, project._data.root.resolve("path1/path2__post.yaml"), HttpMethod.POST),
        DataFile(None, project._data.root.resolve("path1/path2__put.yaml"), HttpMethod.PUT),
        DataFile(None, project._data.root.resolve("path1/path2__delete.yaml"), HttpMethod.DELETE),
        DataFile(Some("2"), project._data.root.resolve("path1/path2__post__2.yaml"), HttpMethod.POST)
      ))
    }

  }

}
