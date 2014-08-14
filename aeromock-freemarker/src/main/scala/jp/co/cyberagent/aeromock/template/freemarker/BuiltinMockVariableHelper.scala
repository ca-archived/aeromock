package jp.co.cyberagent.aeromock.template.freemarker

import java.util.Date

import jp.co.cyberagent.aeromock.core.http.RequestManager

/**
 *
 * @author stormcat24
 */
object BuiltinMockVariableHelper {


  // proxy内から呼び出されるので、signature変更の際は注意
  def getMockCurrentTime(realTime: Date): Date = {
    require(realTime != null)

    RequestManager.getDataMap.get("__now") match {
      case now: Date => now
      case _ => realTime
    }

  }
}
