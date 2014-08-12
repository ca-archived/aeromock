package ameba.aeromock.template.freemarker

import java.util.Date
import ameba.aeromock.core.http.RequestManager
import org.joda.time.format.DateTimeFormat

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