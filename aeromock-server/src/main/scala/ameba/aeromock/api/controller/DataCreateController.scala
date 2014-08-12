package ameba.aeromock.api.controller

import ameba.aeromock.AeromockApiBadRequestException
import ameba.aeromock.config.ConfigHolder
import ameba.aeromock.core.Validations._
import ameba.aeromock.core.http.ParsedRequest
import ameba.aeromock.template.TemplateService

import scalaz.Scalaz._
import scalaz._

object DataCreateController extends AeromockApiController {

  override def renderJson(request: ParsedRequest): Map[String, Any] = {

    val endpointNel = request.formData.get("endpoint") match {
      case None => new AeromockApiBadRequestException(request.url).failureNel[String]
      case Some(value) => {
        (for {
          endpoint <- value |> blank
        } yield (endpoint)).toValidationNel
      }
    }

    val domainNel = request.formData.get("domain") match {
      case None => "localhost".successNel[Throwable]
      case Some(value) => {
        (for {
          domain <- value |> blank
        } yield (domain)).toValidationNel
      }
    }

    val overwriteNel = request.formData.get("overwrite") match {
      case None => false.successNel[Throwable]
      case Some(value) => ("true" == value).successNel
    }

    val serviceNel = ConfigHolder.getTemplateService match {
      case None => new AeromockApiBadRequestException("/data/create").failureNel[TemplateService]
      case Some(value) => value.successNel[Throwable]
    }

    val result = (endpointNel |@| domainNel |@| overwriteNel |@| serviceNel) {
      (endpoint, domain, overwrite, service) =>
//      Map("files" -> List(service.write(Endpoint(endpoint), domain, overwrite)).toString())
      // TODO 新たに空データ作成処理を入れる
      Map("files"-> List.empty)
    }

    result match {
      case Failure(failure) => {
        // TODO Exceptionのマージを検討
        throw failure.list.head
      }
      case Success(value) => value
    }

  }

}
