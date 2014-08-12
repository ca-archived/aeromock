package ameba.aeromock.template.freemarker

import java.io.File
import java.io.FileInputStream
import java.io.Reader
import java.io.StringReader
import java.nio.file.Path
import java.util.regex.Pattern

import ameba.aeromock.AeromockResourceNotFoundException
import ameba.aeromock.helper._
import ameba.aeromock.util.ResourceUtil
import freemarker.cache.TemplateLoader


class AutoEscapeTemplateLoader(templateRootPath: Path) extends TemplateLoader {

  val FTL_PATTERN = Pattern.compile("<#ftl(.+)>")
  val ESCAPE_START = "<#escape __x as __x?html>"
  val ESCAPE_END = "</#escape>"

  override def findTemplateSource(name: String): AnyRef = {
    require(name != null)

    val template = templateRootPath / name
    if (template.exists) template.toFile() else null
  }

  override def getLastModified(templateSource: AnyRef): Long = {
    templateSource.asInstanceOf[File].lastModified()
  }

  override def getReader(templateSource: AnyRef, encoding: String = "UTF-8"): Reader = {

    if (!templateSource.isInstanceOf[File]) {
        throw new IllegalArgumentException("templateSource is a: " + templateSource.getClass().getName());
    }

    val templateFile = templateSource.asInstanceOf[File]

    val fis = new FileInputStream(templateFile)

    val original = ResourceUtil.readInputStream(fis, encoding) match {
      case Some(content) => content
      case None => throw new AeromockResourceNotFoundException(templateRootPath.toString())
    }

    val matcher = FTL_PATTERN.matcher(original)
    val ftl = if (matcher.find()) {
      matcher.replaceFirst("<#ftl$1>" + System.getProperty("line.separator") + ESCAPE_START) + ESCAPE_END
    } else {
      val builder = new StringBuilder
      builder.append(ESCAPE_START)
      builder.append(original)
      builder.append(ESCAPE_END)
      builder.toString
    }

    new StringReader(ftl)
  }

  override def closeTemplateSource(templateSource: AnyRef) {
    // nothing to do
  }

}
