package ameba.aeromock.server.http

import ameba.aeromock.config.entity.Naming
import ameba.aeromock.helper._

import scalaz.Scalaz._
import scalaz.Validation._
import scalaz.ValidationNel

/**
 *
 * @author stormcat24
 */
trait ResponseStatusSupport {

  def createCustomResponse(naming: Naming, dataMap: Map[Any, Any]): ValidationNel[Throwable, Option[CustomResponse]] = {
    dataMap.get(naming.response) match {
      case Some(response) => {
        val map = cast[Map[Any, Any]](response).toValidationNel
        map.flatMap { m =>
          val codeResult = for {
            codeRaw <- m.get("code").toSuccess(new Throwable("'code' not specified"))
            code <- codeRaw |> ((value: Any) => fromTryCatch(value.asInstanceOf[Int]))
          } yield (code)

          val headersResult = m.get("headers") match {
            case None => Map.empty[String, String].success[Throwable]
            case Some(value) => {
              for {
                headers <- value |> ((value: Any) => fromTryCatch(value.asInstanceOf[Map[String, String]]))
              } yield (headers)
            }
          }

          (codeResult.toValidationNel |@| headersResult.toValidationNel) {
            CustomResponse(_, _).some
          }
        }
      }
      case None => none[CustomResponse].successNel[Throwable]
    }
  }
}
