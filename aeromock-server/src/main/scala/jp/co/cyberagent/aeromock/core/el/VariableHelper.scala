package jp.co.cyberagent.aeromock.core.el

/**
 * Helper of variables.
 * @author stormcat24
 */
class VariableHelper(variableMap: Map[String, Any]) {

  val context = new ELContext(variableMap)

  val variableConversion = (value: Any) => {
    value match {
      case s: String => context.eval(s)
      case _ => value
    }
  }

  val variableConversionTuple = (entry: (Any, Any)) => (entry._1, variableConversion(entry._2))

}
