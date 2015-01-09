package jp.co.cyberagent.aeromock.server.http

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.{FullHttpRequest, HttpResponse, HttpResponseStatus}
import jp.co.cyberagent.aeromock.config.Project
import jp.co.cyberagent.aeromock.core.el.VariableHelper
import jp.co.cyberagent.aeromock.core.http.VariableManager
import jp.co.cyberagent.aeromock.data.{DataFileReaderFactory, DataPathResolver}
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.{AeromockApiNotFoundException, AeromockSystemException}
import scaldi.Injector
import scala.collection.JavaConverters._

/**
 * [[jp.co.cyberagent.aeromock.server.http.HttpRequestProcessor]] for JSON API.
 * @author stormcat24
 */
class JsonApiHttpRequestProcessor(implicit inj: Injector) extends HttpRequestProcessor with HttpResponseWriter {

  val project = inject[Project]

  override def process(request: FullHttpRequest)(implicit context: ChannelHandlerContext): HttpResponse = {

    val ajax = project._ajax
    val ajaxRoot = ajax.root
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

    ajax.jsonpCallbackName.flatMap(p => request.parsedRequest.queryParameters.get(p)) match {
      case None => renderJson(response._1, HttpResponseStatus.OK, response._2)
      case Some(callbackName) => renderJsonp(response._1, callbackName, HttpResponseStatus.OK, response._2)
    }

  }
}
