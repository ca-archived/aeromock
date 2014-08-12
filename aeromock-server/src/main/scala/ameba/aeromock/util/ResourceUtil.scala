package ameba.aeromock.util

import java.io.{InputStream, Writer}
import java.nio.file.{Files, Path}

import ameba.aeromock.helper._

import scala.io.Source
import scala.language.reflectiveCalls

object ResourceUtil {

  type Closable = { def close(): Unit }

  def readFile(file: Path, charset: String = "UTF-8"): Option[String] = {
    require(file != null)

    readInputStream(Files.newInputStream(file.withHomeDirectory), charset)
  }

  def readInputStream(is: InputStream, charset: String = "UTF-8"): Option[String] = {
    require(is != null)
    Source.fromInputStream(is, charset) match {
      case null => None
      case resource => Some(processResrouce(resource)(_.mkString))
    }
  }

  def read[A](file: Path, charset: String = "UTF-8"): Option[(InputStream => A) => A] = {
    require(file != null)

    Files.newInputStream(file.withHomeDirectory) match {
      case null => None
      case resource => Some(processResrouce[InputStream, A](resource) _ )
    }
  }

  def write(writer: Writer, content: String) {
    require(writer != null)
    require(content != null)

    processResrouce(writer)(_.write(content))
  }

  def processResrouce[A <: Closable, B](resource: A)(f: A => B) = try {
    f(resource)
  } finally { resource.close }
}
