package jp.co.cyberagent.aeromock

import java.io.File
import java.net.{InetSocketAddress, URLDecoder}
import java.nio.file._
import java.nio.file.attribute.BasicFileAttributes
import java.security.MessageDigest
import java.util.regex.Pattern

import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.multipart.{HttpPostRequestDecoder, MixedAttribute}
import jp.co.cyberagent.aeromock.config.MessageManager
import jp.co.cyberagent.aeromock.core.http.ParsedRequest
import jp.co.cyberagent.aeromock.util.SystemProperty
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.reflect.FieldUtils

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag
import scalaz.Validation
import scalaz.Validation._
import scala.language.reflectiveCalls

/**
 * Package object for various helper.
 * @author stormcat24
 */
package object helper {

  val INSECURE_URI = Pattern.compile( """.*[<>&"].*""")
  val EXTENSION = Pattern.compile( """.*\.(.+)""")
  val WITHOUT_EXTENSION = Pattern.compile("""(.+)\..+""")

  type Closable = { def close(): Unit }
  def processResource[A <: Closable, B](resource: A)(f: A => B) = try {
    f(resource)
  } finally { resource.close }

  def millsToSeconds(end: Long, start: Long): BigDecimal = {
    BigDecimal(end - start) / BigDecimal(1000)
  }

  def getDifferenceSecondsFromNow(pointTime: Long): BigDecimal = millsToSeconds(System.currentTimeMillis(), pointTime)


  def tryo[T](f: => T)(implicit error: Throwable => Option[T] = {
    t: Throwable => None
  }): Option[T] = {
    try {
      Some(f)
    } catch {
      case c: Throwable => error(c)
    }
  }

  def trye[T](f: => T)(implicit onError: Throwable => Either[Throwable, T] = {
    t: Throwable => Left(t)
  }): Either[Throwable, T] = {
    try {
      Right(f)
    } catch {
      case c: Throwable => onError(c)
    }
  }

  def getExtension(value: String): Option[String] = {
    require(value != null)

    val result = EXTENSION.matcher(value)
    result.matches() match {
      case false => None
      case true => Some(result.group(1))
    }
  }

  def getObjectFqdn(target: Any): String = {
    require(target != null)

    target.getClass().getName().replaceAll( """\$$""", "")
  }

  def red(value: String) = s"\u001b[31m${value}\u001b[00m"
  def green(value: String) = s"\u001b[32m${value}\u001b[00m"
  def yellow(value: String) = s"\u001b[33m${value}\u001b[00m"
  def blue(value: String) = s"\u001b[34m${value}\u001b[00m"
  def purple(value: String) = s"\u001b[35m${value}\u001b[00m"
  def lightBlue(value: String) = s"\u001b[36m${value}\u001b[00m"
  def white(value: String) = s"\u001b[37m${value}\u001b[00m"
  def rollingOver(value: String) = s"\u001b[7m${value}\u001b[00m"

  def cast[S: ClassTag](value: Any): Validation[Throwable, S] = {
    val t = implicitly[ClassTag[S]].runtimeClass.asInstanceOf[Class[S]]
    fromTryCatch(t.cast(value))
  }

  // implicit classes START

  implicit class StringContextHelper(val context: StringContext) {

    def message(args: AnyRef*): String = {
      val key = context.parts.iterator.toList.head
      MessageManager.getMessage(key, args.iterator.toList:_*)
    }
  }

  implicit class PathHelper(val path: Path) {

    val fileSystem = FileSystems.getDefault()

    def exists(): Boolean = Files.exists(path)

    def isDirectory(): Boolean = Files.isDirectory(path)

    def withoutExtension(): Path = {

      val matcher = WITHOUT_EXTENSION.matcher(path.toString)

      matcher.matches() match {
        case false => path
        case true => fileSystem.getPath(matcher.group(1))
      }
    }

    def getChildren(): List[Path] = {
      tryo {
        Files.newDirectoryStream(path)
      } match {
        case Some(system) => processResource(system)(_.asScala.toList)
        case None => List.empty
      }
    }

    def getRelativePath(root: Path): Path = {
      val full = path.toString.replace("/", fileSystem.getSeparator())
      val base = root.toString.replace("/", fileSystem.getSeparator())
      fileSystem.getPath(full.replace(base, ""))
    }

    def hasExtension(extension: String): Boolean = {
      require(extension != null)

      path.getFileName().toString.endsWith(s".$extension")
    }

    def +(token: String): Path = Paths.get(path.toString + token)

    def /(childPath: String): Path = {
      require(StringUtils.isNotBlank(childPath))

      val optimized = "." + fileSystem.getSeparator() + childPath.replace("/", fileSystem.getSeparator())
      path.resolve(optimized).normalize()
    }

    def /(childPath: Path): Path = {
      require(childPath != null)
      /(childPath.toString())
    }

    def filterChildren(regExp: String): List[Path] = {
      require(regExp != null)
      if (!isDirectory) {
        throw new IllegalStateException(s"${path.toString()} is not directory.")
      }

      val pattern = Pattern.compile(regExp)
      val buf = new ListBuffer[Path]
      Files.walkFileTree(path, new SimpleFileVisitor[Path] {
        override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
          val matcher = pattern.matcher(file.getFileName().toString())
          if (matcher.find()) {
            buf += file
          }
          FileVisitResult.CONTINUE
        }
      })

      buf.toList
    }

    def withHomeDirectory(): Path = {

      if (path.toString().startsWith("~")) {
        SystemProperty.getValue("user.home") match {
          case Some(home) => {
            val replaceExp = if (System.getProperty("os.name").contains("Windows")) {
              home.replace("""\""", """\\""")
            } else {
              home
            }
            fileSystem.getPath(path.toString().replaceFirst("^~", replaceExp))
          }
          case None => throw new IllegalStateException("can't get system property 'user.home'.")
        }
      } else {
        path
      }
    }

    def toCheckSum(): String = {
      val is = Files.newInputStream(path, StandardOpenOption.READ)
      val buf = new Array[Byte](4096)

      val md = MessageDigest.getInstance("MD5")

      var len = 0
      while ({len = is.read(buf, 0, buf.length); len >= 0}) md.update(buf, 0, len)
      md.digest().map("%02x" format _).mkString
    }

    def getExtension(): Option[String] = helper.getExtension(path.getFileName.toString)
  }

  implicit class FullHttpRequestHelper(original: HttpRequest) {

    lazy val decoded = URLDecoder.decode(original.getUri(), "UTF-8")

    lazy val requestUri = if (decoded.contains("?")) decoded.substring(0, decoded.indexOf("?")) else decoded

    lazy val queryString = if (decoded.contains("?")) decoded.substring(decoded.indexOf("?") + 1, decoded.length()) else ""

    lazy val parsedRequest = {
      import io.netty.handler.codec.http.HttpMethod._

      // query parameter
      val urlTuple = if (decoded.contains("?")) {
        val uri = decoded.substring(0, decoded.indexOf("?"))
        (uri, decoded.substring(decoded.indexOf("?") + 1, decoded.length()).split("&").map(s => {
          val pair = s.split("=")
          if (pair.length > 1) (pair(0), pair(1)) else (pair(0), "")
        }).toMap)
      } else {
        (decoded, Map.empty[String, String])
      }

      val formData = if (Array(POST, PUT, DELETE, PATCH).contains(original.getMethod())) {
        val postDecoder = new HttpPostRequestDecoder(original)
        (postDecoder.getBodyHttpDatas().asScala.collect {
          case a: MixedAttribute => (a.getName() -> a.getValue())
        }).toMap
      } else {
        Map.empty[String, String]
      }

      ParsedRequest(urlTuple._1, urlTuple._2, formData)
    }

    lazy val extension = getExtension(parsedRequest.url)

    def checkSecurity() {
      // security guard
      decoded.replace("/", File.separator) match {
        case s if s.contains(File.separator + ".") ||
          s.contains("." + File.separator) ||
          s.startsWith(".") ||
          s.endsWith(".") ||
          INSECURE_URI.matcher(s).matches() => throw new AeromockInvalidRequestException(decoded)
        case _ =>
      }
    }

    def getWithoutExtensionUrl = {
      val result = WITHOUT_EXTENSION.matcher(parsedRequest.url)
      result.matches() match {
        case false => None
        case true => Some(result.group(1))
      }
    }

    def toVariableMap(): Map[String, Any] = {
      import io.netty.handler.codec.http.HttpHeaders.Names

      val namesClass = classOf[Names]
      namesClass.getFields().toArray.map {
        f =>
          val headerKey = FieldUtils.readDeclaredStaticField(namesClass, f.getName, true)
          val headerValue = original.headers().get(headerKey.toString)
          (f.getName, if (headerValue == null) "" else headerValue)
      }.toMap
    }
  }

  implicit class ExternalInetSocketAddress(source: InetSocketAddress) {

    def toVariableMap(): Map[String, String] = {
      Map(
        "REMOTE_ADDR" -> source.getAddress().getHostAddress(),
        "REMOTE_HOST" -> source.getHostName()
      )
    }
  }

  // implicit classes END

}
