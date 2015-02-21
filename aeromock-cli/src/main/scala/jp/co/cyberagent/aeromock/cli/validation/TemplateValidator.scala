package jp.co.cyberagent.aeromock.cli.validation

import jp.co.cyberagent.aeromock.config.{Data, Template}
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.template.TemplateService
import scaldi.{Injector, Injectable}

/**
 *
 * @author stormcat24
 */
class TemplateValidator(implicit inj: Injector) extends AnyRef with Injectable {

  def validate(template: Template, data: Data): Unit = {

    // TODO 拡張子取るためだけに取るのイケてない
    val templateService = inject[Option[TemplateService]].get

    template.contexts.map { context =>
      val contextRoot = s"http://${context.domain}:${context.port}"

      // TODO 拡張子フィルタリング
      context.root.filterChildren(s".${templateService.extension}$$").map { templatePath =>
        println(templatePath)
      }
    }
  }
}
