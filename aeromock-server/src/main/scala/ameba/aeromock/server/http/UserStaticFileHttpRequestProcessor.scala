package ameba.aeromock.server.http

import java.nio.file.Path

import ameba.aeromock.config.entity.Project
import ameba.aeromock.helper._
import ameba.aeromock.core.http.ParsedRequest

object UserStaticFileHttpRequestProcessor extends StaticFileHttpRequestProcessor {

  override protected def getStaticFile(project: Project, parsedRequest: ParsedRequest): Path = {
    project._static.root / parsedRequest.url
  }

}
