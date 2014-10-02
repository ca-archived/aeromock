package jp.co.cyberagent.aeromock

import jp.co.cyberagent.aeromock.helper._
import org.joda.time.DateTime

/**
 *
 * @author stormcat24
 */
object AeromockInfo {

  val version = "0.1.4"

  val currentYear = DateTime.now().getYear()

  val defaultListenPort = 3183

  val splash = s"""
***************************************************************************
${blue("""
              ___                                        __
             /   | ___  _________  ____ ___  ____  _____/ /__
            / /| |/ _ \/ ___/ __ \/ __ `__ \/ __ \/ ___/ //_/
           / ___ /  __/ /  / /_/ / / / / / / /_/ / /__/ ,<
          /_/  |_\___/_/   \____/_/ /_/ /_/\____/\___/_/|_|
""")}

  Aeromock Version ${lightBlue(AeromockInfo.version)}
  Copyright 2013-${currentYear} ${green("CyberAgent, Inc.")} All Rights Reserved.

  "Aeromock" is a mock http server for frontend engineer.
  You can get http response if only you write template and data file.
***************************************************************************
  """

}
