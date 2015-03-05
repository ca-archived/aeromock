package jp.co.cyberagent.aeromock.cli

/**
 *
 * @author stormcat24
 */
package object validation {

  case class TestSummary(totalTemplates: Int, numSuccess: Int, numFailed: Int, numSkip: Int) {

    def passed = numFailed <= 0
  }

}
