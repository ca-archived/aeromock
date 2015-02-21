package jp.co.cyberagent.aeromock.cli.job

import jp.co.cyberagent.aeromock.cli.Job

/**
 * Job definition object.
 * @author stormcat24
 */
object CliJobs {

  val jobs = Seq(
    classOf[ValidationJob]
  )

  lazy val availableJobs = {
    jobs.collect {
      case job if job.getAnnotation(classOf[Job]) != null => (job.getAnnotation(classOf[Job]), job)
    }
  }
}
