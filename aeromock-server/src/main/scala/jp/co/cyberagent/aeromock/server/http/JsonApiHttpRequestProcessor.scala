package jp.co.cyberagent.aeromock.server.http

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.{FullHttpRequest, HttpResponse, HttpResponseStatus}
import jp.co.cyberagent.aeromock.config.Project
import jp.co.cyberagent.aeromock.core.el.VariableHelper
import jp.co.cyberagent.aeromock.core.http.VariableManager
import jp.co.cyberagent.aeromock.data.{DataFileReaderFactory, DataPathResolver}
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.{AeromockApiNotFoundException, AeromockSystemException}
import scala.collection.JavaConverters._

/**
 * [[jp.co.cyberagent.aeromock.server.http.HttpRequestProcessor]] for JSON API.
 * @author stormcat24
 */
object JsonApiHttpRequestProcessor extends HttpRequestProcessor with HttpResponseWriter {

  override def process(project: Project, request: FullHttpRequest)
    (implicit context: ChannelHandlerContext): HttpResponse = {

    val ajaxRoot = project._ajax.root
    val naming = project._naming

    val dataFile = DataPathResolver.resolve(ajaxRoot, request.parsedRequest, naming) match {
      case None => throw new AeromockApiNotFoundException(request.parsedRequest.url)
      case Some(file) => file
    }

    val dataMap = DataFileReaderFactory.create(dataFile) match {
      case None => throw new AeromockSystemException(s"Cannot read Data file '${dataFile.toString}'")
      case Some(reader) => reader.readFile(dataFile)
    }

    val variableHelper = new VariableHelper(VariableManager.getRequestMap ++ VariableManager.getOriginalVariableMap().asScala)
    val responseWriter = JsonApiResponseWriterFactory.create(project, variableHelper, dataMap)

    val response = responseWriter.write
    renderJson(response._1, HttpResponseStatus.OK, response._2)
  }
}
