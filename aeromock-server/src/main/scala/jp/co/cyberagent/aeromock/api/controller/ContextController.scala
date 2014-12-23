package jp.co.cyberagent.aeromock.api.controller

import java.nio.file.Path

import jp.co.cyberagent.aeromock.api.AeromockApiController
import jp.co.cyberagent.aeromock.config.Project
import jp.co.cyberagent.aeromock.core.http.Endpoint
import jp.co.cyberagent.aeromock.data.DataFileService
import jp.co.cyberagent.aeromock.helper._
import scaldi.Injector

/**
 *
 * @author stormcat24
 */
class ContextController(implicit val injector: Injector) extends AeromockApiController {

  get("/aeromock/api/contexts") { request =>

    val project = inject[Project]
    val contexts = project._template.contexts

    if (contexts.isEmpty) {
      Map()
    } else {
      Map(
        "contexts" -> contexts.map(context => {
          Map("domain" -> context.domain) ++ fetchFiles(context.root)
        })
      )
    }
  }

  private def fetchFiles(root: Path): Map[String, Any] = {
    val grouped = root.getChildren.groupBy(_.isDirectory)

    val dirs = grouped.get(true).map(dirs => dirs.map(_.getFileName.toString))

    grouped.get(false).map(files => {
      files.map(file => {
        val dataFiles = if (file.isDirectory) {
          val endpoint = Endpoint(file.getRelativePath(root).withoutExtension.toString)
          DataFileService.getRelatedDataFiles(endpoint).map(d => {
            Map(
              "id" -> d.id.getOrElse(null),
              "method" -> d.method.name
            )
          })
        } else {
          List.empty
        }
      })
    })
    root.getChildren.map(file => {

      // TODO テンプレ拡張子じゃないのを除外

      val dataFiles = if (file.isDirectory) {
        val endpoint = Endpoint(file.getRelativePath(root).withoutExtension.toString)
        DataFileService.getRelatedDataFiles(endpoint).map(d => {
          Map(
            "id" -> d.id.getOrElse(null),
            "method" -> d.method.name
          )
        })
      } else {
        List.empty
      }

      Map(
        "path" -> file.getFileName.toString,
        "data_files" -> dataFiles
      )
    })

    Map(
      "directories" -> (dirs match {
        case Some(list) => list
        case None => List.empty
      }),
      "files" -> List.empty
    )
  }
}
