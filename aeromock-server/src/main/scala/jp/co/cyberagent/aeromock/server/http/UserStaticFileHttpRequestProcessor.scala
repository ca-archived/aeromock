package jp.co.cyberagent.aeromock.server.http

import java.nio.file.Path

import jp.co.cyberagent.aeromock.config.entity.Project
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.core.http.ParsedRequest

object UserStaticFileHttpRequestProcessor extends StaticFileHttpRequestProcessor {

  override protected def getStaticFile(project: Project, parsedRequest: ParsedRequest): Path = {
    project._static.root / parsedRequest.url
  }

}
