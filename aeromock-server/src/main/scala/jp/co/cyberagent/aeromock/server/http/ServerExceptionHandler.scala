package jp.co.cyberagent.aeromock.server.http

import io.netty.channel.ChannelHandlerContext
import jp.co.cyberagent.aeromock._
import jp.co.cyberagent.aeromock.server.AccessLog
import jp.co.cyberagent.aeromock.server.http.error._

object ServerExceptionHandler extends HttpResponseWriter {

  def handle(t: Throwable)(implicit context: ChannelHandlerContext) {
    if (context.channel().isActive()) {
      val errorPage = (t match {
        // AeromockNotFoundException
        case e: AeromockNotFoundException => NotFoundErrorPage(e)

        // AeromockConfigurationException
        case e: AeromockConfigurationException => ConfigurationErrorPage(e)

        // AeromockBadImplementation
        case e: AeromockNoneRelatedDataException => NoneRelatedDataErrorPage(e)
        case e: AeromockBadImplementation => BadImplementationErrorPage(e)

        // AeromockBadUsingException
        case e: AeromockBadUsingException => BadUsingErrorPage(e)

        // AeromockMethodNotAllowedException
        case e: AeromockMethodNotAllowedException => MethodNotAllowedErrorPage(e)

        case _ => SystemErrorPage(t)
      })

      AccessLog.writeAccessLog(errorPage.responseStatus.code())
      renderHtml(errorPage.render(), errorPage.responseStatus, None)
    }
  }

}
