package jp.co.cyberagent.aeromock.msgpack

import jp.co.cyberagent.aeromock.config.Project
import jp.co.cyberagent.aeromock.core.el.VariableHelper
import jp.co.cyberagent.aeromock.core.http.{AeromockHttpRequest, VariableManager}
import jp.co.cyberagent.aeromock.data.{DataFileReaderFactory, DataPathResolver}
import jp.co.cyberagent.aeromock.server.http.{JsonApiResponseWriterFactory, RenderResult, ResponseDataSupport}
import jp.co.cyberagent.aeromock.{AeromockApiNotFoundException, AeromockSystemException}
import org.msgpack.ScalaMessagePack
import scaldi.{Injectable, Injector}

import scala.collection.JavaConverters._

/**
 *
 * @author stormcat24
 */
object MessagepackResponseService extends AnyRef with Injectable with ResponseDataSupport {

  def render(request: AeromockHttpRequest)(implicit inj: Injector): RenderResult[Array[Byte]] = {
    val project = inject[Project]
    val naming = project._naming
    val messagepack = project._messagepack

    val dataFile = DataPathResolver.resolve(messagepack.root, request, naming) match {
      case None => throw new AeromockApiNotFoundException(request.url)
      case Some(file) => file
    }

    val dataMap = DataFileReaderFactory.create(dataFile) match {
      case None => throw new AeromockSystemException(s"Cannot read Data file '${dataFile.toString}'")
      case Some(reader) => reader.readFile(dataFile)
    }

    val variableHelper = new VariableHelper(VariableManager.getRequestMap ++ VariableManager.getOriginalVariableMap().asScala)
    val responseWriter = JsonApiResponseWriterFactory.create(project, variableHelper, dataMap)

    val response = responseWriter.write
    val dynamicValue = MessagepackValue.fromIterable(response._1.asInstanceOf[Iterable[_]])

    val serialized = ScalaMessagePack.writeV(dynamicValue)
    RenderResult(serialized, response._2, false)
  }
}
