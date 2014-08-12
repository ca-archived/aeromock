package ameba.aeromock.server.http

import ameba.aeromock._
import ameba.aeromock.server.AccessLog
import ameba.aeromock.server.http.error._
import io.netty.channel.ChannelHandlerContext

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