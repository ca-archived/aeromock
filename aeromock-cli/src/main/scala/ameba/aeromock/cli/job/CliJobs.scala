package ameba.aeromock.cli.job

import ameba.aeromock.cli.Job

/**
 * Job definition object.
 * @author stormcat24
 */
object CliJobs {

  val jobs = Seq(
    classOf[ViewCheckJob]
  )

  lazy val availableJobs = {
    jobs.collect {
      case job if job.getAnnotation(classOf[Job]) != null => (job.getAnnotation(classOf[Job]), job)
    }
  }
}
