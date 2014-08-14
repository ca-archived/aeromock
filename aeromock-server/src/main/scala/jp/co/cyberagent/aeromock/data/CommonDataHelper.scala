package jp.co.cyberagent.aeromock.data

import java.nio.file.{Files, Path}

import jp.co.cyberagent.aeromock.config.entity.Naming
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.core.http.{ParsedRequest, RequestManager}
import jp.co.cyberagent.aeromock.core.script.GroovyScriptRunner
import groovy.lang.Binding

import scala.collection.JavaConverters._

/**
 * Common data helper.
 * @param naming [[jp.co.cyberagent.aeromock.config.entity.Naming]]
 * @author stormcat24
 */
class CommonDataHelper(naming: Naming) {

  def getMergedDataMap(rootDir: Path, script: Path): Map[Any, Any] = {

    val dataPaths = if (Files.exists(script)) {
      val binding = new Binding
      RequestManager.getRequestMap.foreach(pair => binding.setVariable(pair._1, pair._2))

      val scriptRunner = new GroovyScriptRunner[java.util.List[String]](script)
      val result = scriptRunner.run(binding)
      if (result.isEmpty) List.empty[String] else result.asScala.toList
    } else {
      List.empty
    }

    dataPaths.toSet.foldLeft(Map.empty[Any, Any])((left, dataPath) => {
      val map = DataPathResolver.resolve(rootDir, ParsedRequest(s"/$dataPath", Map.empty, Map.empty)) match {
        case Some(file) if file.exists() => {
          DataFileReaderFactory.create(file) match {
            case None => Map.empty[Any, Any]
            case Some(reader) => {
              reader.readFile(file).collect {
                case (key, value) => (key, value)
              }.toMap
            }
          }
        }
        case _ => Map.empty[Any, Any]
      }
      mergeAdditional(left, map)
    })
  }

  /**
   * Merge map by using {@code additional} property.
   * @param left Map of Left side
   * @param right Map of Right side
   * @return merged map
   */
  def mergeAdditional(left: Map[Any, Any], right: Map[Any, Any]): Map[Any, Any] = {

    right.foldLeft(left)((merged, entry) => {
      val pair = entry match {
        case (key, value: Map[Any, Any] @unchecked) => {
          value.get(naming.additional) match {
            case Some(true) => {
              merged.get(key) match {
                case Some(map: Map[_, _]) => {
                  (key, map ++ value - naming.additional)
                }
                case _ => (key, value)
              }
            }
            case _ => (key, value)
          }
        }
        case (key, value) => (key, value)
      }
      merged + pair
    })
  }

}
