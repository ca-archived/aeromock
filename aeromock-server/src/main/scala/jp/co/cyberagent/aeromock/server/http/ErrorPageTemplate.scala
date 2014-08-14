package jp.co.cyberagent.aeromock.server.http.error

import java.io.{PrintWriter, StringWriter}
import java.util.Locale

import io.netty.handler.codec.http.HttpResponseStatus
import jp.co.cyberagent.aeromock._
import jp.co.cyberagent.aeromock.config.MessageManager
import org.yaml.snakeyaml.error.YAMLException

import scalaz.Scalaz._

/**
 *
 * @author stormcat24
 */
sealed abstract class ErrorPageTemplate[T <: Throwable](throwable: T) {

  def render(): String = {
    s"""|<!DOCTYPE html>
      |<html lang="${Locale.getDefault.getDisplayLanguage}">
      |  <head>
      |    <meta charset="utf-8">
      |    <title>Aeromock(${responseStatus.toString()})</title>
      |    <meta name="viewport" content="width=device-width, initial-scale=1.0">
      |
      |    <link href="/aeromock/components/flat-ui-official/bootstrap/css/bootstrap.css" rel="stylesheet">
      |    <link href="/aeromock/components/flat-ui-official/css/flat-ui.css" rel="stylesheet">
      |
      |    <style type="text/css">
      |      .trace {
      |        line-height:18px;
      |      }
      |      .label-top {
      |        border-bottom: double;
      |      }
      |    </style>
      |    <link rel="shortcut icon" href="favicon.ico">
      |  </head>
      |
      |  <body>
      |    <div class="container">
      |      <div class="row lead label-top">HTTP ${responseStatus.toString()}</div>
      |      ${renderTopic()}
      |      ${renderOperationArea()}
      |      ${renderStackTrace()}
      |    </div>
      |  </body>
      |</html>""".stripMargin
  }

  private def renderTopic(): String = {
    val topic = writeTopic().replace(System.getProperty("line.separator"), """<br/>""").replace("\t", "&nbsp;&nbsp;")
    println(topic)
    s"""
       |<div class="row bg-danger text-danger well well-sm small trace">
       |  <p><em>${topic}</em></p>
       |</div>
     """.stripMargin
  }

  private def renderOperationArea(): String = {
    writeOperationArea() match {
      case None => ""
      case Some(value) => {
        s"""
           |<div class="row">
           |  <span class="strong">Operation Area</span>
           |  $value
           |</div>
         """.stripMargin
      }
    }
  }

  private def renderStackTrace(): String = {
    writeStackTrace() match {
      case None => ""
      case Some(value) => {
        s"""
           |<hr/>
           |<div class="row">
           |  <span class="strong">Error Stacktrace</span>
           |  <p class="bg-info text-info well well-sm small trace">
           |  ${value.replace(System.getProperty("line.separator"), """<br/>""").replace("\t", "&nbsp;&nbsp;")}
           |  </p>
           |</div>
         """.stripMargin
      }
    }
  }

  protected def writeTopic(): String = throwable.getMessage

  protected def writeOperationArea(): Option[String] = None

  protected def writeStackTrace(): Option[String] = {
    val sw = new StringWriter
    val pw = new PrintWriter(sw)
    throwable.printStackTrace(pw)
    sw.toString.some
  }

  def responseStatus: HttpResponseStatus
}

case class SystemErrorPage(throwable: Throwable) extends ErrorPageTemplate(throwable) {
  throwable.printStackTrace()
  override protected def writeTopic(): String = {
    s"${MessageManager.getMessage("systemerror.message")}<br/>" + super.writeTopic
  }
  override def responseStatus: HttpResponseStatus = HttpResponseStatus.INTERNAL_SERVER_ERROR
}

case class BadUsingErrorPage(throwable: AeromockBadUsingException) extends ErrorPageTemplate(throwable) {
  override def responseStatus: HttpResponseStatus = HttpResponseStatus.BAD_REQUEST
}

case class BadImplementationErrorPage(throwable: AeromockBadImplementation) extends ErrorPageTemplate(throwable) {
  override protected def writeTopic(): String = {
    val message = Option(throwable.getCause) match {
      case Some(e: YAMLException) => e.getMessage.replace(";", System.getProperty("line.separator"))
      case Some(e) => e.getMessage
      case None => throwable.getMessage
    }

    s"${super.writeTopic}<br/>${message}"
  }
  override def responseStatus: HttpResponseStatus = HttpResponseStatus.FORBIDDEN
}

case class NoneRelatedDataErrorPage(throwable: AeromockNoneRelatedDataException) extends ErrorPageTemplate(throwable) {
  override def responseStatus: HttpResponseStatus = HttpResponseStatus.FORBIDDEN
  override def writeOperationArea(): Option[String] = {
//    s"""
//       |<div>
//       |  <button class="btn btn-info">Create Data</button>
//       |</div>
//     """.some
    // TODO Create data link
    None
  }
}

case class ConfigurationErrorPage(throwable: AeromockConfigurationException) extends ErrorPageTemplate(throwable) {
  override protected def writeTopic(): String = {
    val builder = new StringBuilder("設定ファイルに問題があります。<br/>")
    builder ++= "<ul>"
    throwable.errors.list.foreach(s => builder ++= s"<li>$s</li>")
    builder ++= "</ul>"
    builder.toString
  }
  override def responseStatus: HttpResponseStatus = HttpResponseStatus.FORBIDDEN
}

case class MethodNotAllowedErrorPage(throwable: AeromockMethodNotAllowedException) extends ErrorPageTemplate(throwable) {
  override def responseStatus: HttpResponseStatus = HttpResponseStatus.METHOD_NOT_ALLOWED
  override protected def writeStackTrace(): Option[String] = None
}

case class NotFoundErrorPage(throwable: AeromockNotFoundException) extends ErrorPageTemplate(throwable) {
  override def responseStatus: HttpResponseStatus = HttpResponseStatus.NOT_FOUND
  override protected def writeStackTrace(): Option[String] = None
}
