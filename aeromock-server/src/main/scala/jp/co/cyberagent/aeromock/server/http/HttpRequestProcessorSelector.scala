package jp.co.cyberagent.aeromock.server.http

import io.netty.handler.codec.http.HttpRequest
import jp.co.cyberagent.aeromock.AeromockConfigurationException
import jp.co.cyberagent.aeromock.config.{Project, ConfigHolder}
import jp.co.cyberagent.aeromock.helper._

import scalaz._

object HttpRequestProcessorSelector {

  def select(project: Project, request: HttpRequest): HttpRequestProcessor = {

    request.parsedRequest.url match {
      // Aeromockのfavicon
      case "/favicon.ico" => AeromockStaticFileHttpRequestProcessor
      // スラッシュのみ（index.htmlと同義）
      case "/" => AeromockStaticFileHttpRequestProcessor
      // Aeromock APIのリクエスト
      case url if url.startsWith("/aeromock/api/") => AeromockApiHttpRequestProcessor
      // Aeromockの静的コンテンツ
      case url if url.startsWith("/aeromock/") => AeromockStaticFileHttpRequestProcessor
      case url => {
        // staticで存在チェック
        //   true => そのまま返す
        //   false =>
        //     拡張子を外したURL
        //     テンプレをチェック
        //      なければAJAXをチェック

        val staticInfo = project.static match {
          case Success(Some(value)) => if ((value.root / url).exists()) Some(UserStaticFileHttpRequestProcessor) else None
          case Failure(errors) => throw new AeromockConfigurationException(project.projectConfig, errors)
          case _ => None
        }


        val templateInfo = project.template match {
          case Success(Some(value)) => {
            val templateService = ConfigHolder.getTemplateService.get
            if ((value.root / url + templateService.extension).exists) Some(TemplateHttpRequestProcessor) else None
          }
          case Failure(errors) => throw new AeromockConfigurationException(project.projectConfig, errors)
          case _ => None
        }

        (staticInfo, templateInfo) match {
          case (_, Some(value)) => value
          case (Some(value), _) => value
          case (None, None) => JsonApiHttpRequestProcessor
        }
      }
    }
  }
}
