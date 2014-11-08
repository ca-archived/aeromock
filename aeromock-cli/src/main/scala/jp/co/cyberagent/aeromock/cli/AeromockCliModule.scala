package jp.co.cyberagent.aeromock.cli

import scaldi.Module

/**
 *
 * @author stormcat24
 */
class AeromockCliModule extends Module {

  bind [CliJobSelector] toProvider new CliJobSelector
}
