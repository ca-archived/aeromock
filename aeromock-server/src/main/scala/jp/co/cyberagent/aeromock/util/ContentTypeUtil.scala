package jp.co.cyberagent.aeromock.util

object ContentTypeUtil {

  // FileTypeMap.getDefaultFileTypeMap がOSXでポンコツのため作成

  val contentTypeMap = Map(
    "txt" -> "text/plain",
    "css" -> "text/css",
    "csv" -> "text/csv",
    "tsv" -> "text/tab-separated-values",
    "rtf" -> "text/richtext",
    "html" -> "text/html",
    "js" -> "application/javascript",
    "pdf" -> "application/pdf",
    "zip" -> "application/zip",
    "jpeg" -> "image/jpeg",
    "jpg" -> "image/jpeg",
    "gif" -> "image/gif",
    "png" -> "image/png",
    "swf" -> "application/x-shockwave-flash",
    "pdf" -> "application/pdf",
    "mp3" -> "audio/mp3",
    "swa" -> "audio/mp3",
    "3g2" -> "audio/3gpp2",
    "3gp2" -> "audio/3gpp2",
    "sdp" -> "application/sdp",
    "mp4" -> "audio/mp4",
    "tif" -> "image/tiff",
    "tiff" -> "image/tiff",
    "wav" -> "audio/x-wav",
    "mpeg" -> "video/mpeg",
    "mpg" -> "video/mpeg",
    "m1s" -> "video/mpeg",
    "m1v" -> "video/mpeg",
    "m1a" -> "video/mpeg",
    "m75" -> "video/mpeg",
    "m15" -> "video/mpeg",
    "mp2" -> "video/mpeg",
    "mpm" -> "video/mpeg",
    "mpv" -> "video/mpeg",
    "mpa" -> "video/mpeg",
    "avi" -> "video/avi"
  )

  def getContentType(extension: String): Option[String] = contentTypeMap.get(extension)
}
