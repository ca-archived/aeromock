package jp.co.cyberagent.aeromock.server.http

import java.nio.file.Path
import jp.co.cyberagent.aeromock.config.Project
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.core.http.ParsedRequest
import scaldi.Injector

class UserStaticFileHttpRequestProcessor(implicit inj: Injector) extends StaticFileHttpRequestProcessor {

  override val project = inject[Project]

  override protected def getStaticFile(project: Project, parsedRequest: ParsedRequest): Path = {
    project._static.root / parsedRequest.url
  }

}
