package jp.co.cyberagent.aeromock.cli.option

import jp.co.cyberagent.aeromock.test.SpecSupport
import org.specs2.mutable.{Tables, Specification}

/**
 *
 * @author stormcat24
 */
class CliCommandSpec extends Specification with Tables with SpecSupport {

  "apply" in {
    "commandLine"                | "help" | "version" | "debug" | "configFile"  | "job"         | "arguments"       | "jobOptions"              |
    ""                           ! false  ! false     ! false   ! None          ! None          ! Seq.empty[String] ! Map.empty[String, String] |
    "-h"                         ! true   ! false     ! false   ! None          ! None          ! Seq.empty[String] ! Map.empty[String, String] |
    "-v"                         ! false  ! true      ! false   ! None          ! None          ! Seq.empty[String] ! Map.empty[String, String] |
    "-d"                         ! false  ! false     ! true    ! None          ! None          ! Seq.empty[String] ! Map.empty[String, String] |
    "-h -v -d"                   ! true   ! true      ! true    ! None          ! None          ! Seq.empty[String] ! Map.empty[String, String] |
    "test"                       ! false  ! false     ! false   ! None          ! Some("test")  ! Seq.empty[String] ! Map.empty[String, String] |
    "test -c a.txt"              ! false  ! false     ! false   ! Some("a.txt") ! Some("test")  ! Seq.empty[String] ! Map.empty[String, String] |
    "test -c a.txt -h -v -d"     ! true   ! true      ! true    ! Some("a.txt") ! Some("test")  ! Seq.empty[String] ! Map.empty[String, String] |
    "test aaa -c a.txt -h -v -d -D xxx=yyy" ! true   ! true      ! true    ! Some("a.txt") ! Some("test")  ! Seq("aaa")        ! Map("xxx" -> "yyy") |> {
      (commandLine, help, version, debug, configFile, job, arguments, jobOptions) => {

        val actual = CliCommand(commandLine.split(" "))
        actual.help must_== help
        actual.version must_== version
        actual.debug must_== debug
        actual.configFile must_== configFile
        actual.job must_== job
        actual.jobOperation.arguments must_== arguments
        actual.jobOperation.jobOptions must_== jobOptions
      }
    }
  }
}
