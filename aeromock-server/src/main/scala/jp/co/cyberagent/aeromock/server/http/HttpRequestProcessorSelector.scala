package jp.co.cyberagent.aeromock.server.http

import io.netty.handler.codec.http.HttpRequest
import jp.co.cyberagent.aeromock.AeromockConfigurationException
import jp.co.cyberagent.aeromock.config.Project
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.template.TemplateService
import scaldi.{Injectable, Injector}

import scalaz._
import Scalaz._

object HttpRequestProcessorSelector extends AnyRef with Injectable {

  def select(request: HttpRequest)(implicit inj: Injector): HttpRequestProcessor = {

    request.requestUri match {
      // Aeromockのfavicon
      case "/favicon.ico" => inject[AeromockStaticFileHttpRequestProcessor]
      // スラッシュのみ（index.htmlと同義）
      case "/" => inject[AeromockStaticFileHttpRequestProcessor]
      // Aeromock APIのリクエスト
      case url if url.startsWith("/aeromock/api/") => inject[AeromockApiHttpRequestProcessor]
      // Aeromockの静的コンテンツ
      case url if url.startsWith("/aeromock/") => inject[AeromockStaticFileHttpRequestProcessor]
      case url => {
        // staticで存在チェック
        //   true => そのまま返す
        //   false =>
        //     拡張子を外したURL
        //     テンプレをチェック
        //     protobufをチェック
        //      なければAJAXをチェック

        val project = inject[Project]

        val staticInfo = project.static match {
          case Success(Some(value)) => if ((value.root / url).exists()) inject[UserStaticFileHttpRequestProcessor].some else None
          case Failure(errors) => throw new AeromockConfigurationException(project.projectConfig, errors)
          case _ => None
        }

        val protobufInfo = project.protobuf match {
          case Success(Some(value)) => {
            val protoPath = value.apiPrefix match {
              case Some(apiPrefix) => (value.root / apiPrefix / url + ".proto")
              case None => (value.root / url + ".proto")
            }
            if (protoPath.exists) inject[ProtobufResponseWriter].some else None
          }
          case Failure(errors) => throw new AeromockConfigurationException(project.projectConfig, errors)
          case _ => None
        }

        val templateInfo = project.template match {
          case Success(Some(value)) => {
            val templateService = inject[Option[TemplateService]].get
            if ((value.root / url + templateService.extension).exists) Some(inject[TemplateHttpRequestProcessor]) else None
          }
          case Failure(errors) => throw new AeromockConfigurationException(project.projectConfig, errors)
          case _ => None
        }

        (staticInfo, protobufInfo, templateInfo) match {
          case (_, _, Some(value)) => value
          case (_, Some(value), _) => value
          case (Some(value), _, _) => value
          case (None, None, None) => inject[JsonApiHttpRequestProcessor]
        }
      }
    }
  }
}
