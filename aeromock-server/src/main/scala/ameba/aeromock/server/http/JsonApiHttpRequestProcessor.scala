package ameba.aeromock.server.http

import ameba.aeromock.config.entity.Project
import ameba.aeromock.core.builtin.BuiltinVariableHelper
import ameba.aeromock.core.http.RequestManager
import ameba.aeromock.data.{DataFileReaderFactory, DataPathResolver}
import ameba.aeromock.helper._
import ameba.aeromock.{AeromockApiNotFoundException, AeromockSystemException}
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.{FullHttpRequest, HttpResponse, HttpResponseStatus}

/**
 * [[ameba.aeromock.server.http.HttpRequestProcessor]] for JSON API.
 * @author stormcat24
 */
object JsonApiHttpRequestProcessor extends HttpRequestProcessor with HttpResponseWriter {

  override def process(project: Project, request: FullHttpRequest)
    (implicit context: ChannelHandlerContext): HttpResponse = {

    val ajaxRoot = project._ajax.root

    val dataFile = DataPathResolver.resolve(ajaxRoot, request.parsedRequest) match {
      case None => throw new AeromockApiNotFoundException(request.parsedRequest.url)
      case Some(file) => file
    }

    val dataMap = DataFileReaderFactory.create(dataFile) match {
      case None => throw new AeromockSystemException(s"Cannot read Data file '${dataFile.toString}'")
      case Some(reader) => reader.readFile(dataFile)
    }

    val variableHelper = new BuiltinVariableHelper(RequestManager.getRequestMap)
    val responseWriter = JsonApiResponseWriterFactory.create(project, variableHelper, dataMap)

    val response = responseWriter.write
    renderJson(response._1, HttpResponseStatus.OK, response._2)
  }
}
