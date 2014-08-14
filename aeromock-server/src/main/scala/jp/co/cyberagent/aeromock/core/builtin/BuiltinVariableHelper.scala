package jp.co.cyberagent.aeromock.core.builtin

import org.apache.commons.lang3.StringUtils

/**
 * Helper of builtin variables.
 * @author stormcat24
 */
class BuiltinVariableHelper(variableMap: Map[String, Any]) {

  val VARIABLE_PATTERN = """\$\{([0-9a-zA-Z_]+)\}""".r

  val variableConversion = (value: Any) => {
    value match {
      case s: String => {
        val variables = VARIABLE_PATTERN.findAllMatchIn(s).toList

        if (variables.isEmpty) {
          s
        } else if (variables.size == 1 && variables.head.matched == s) {
          // return as it as, not change type of builtin variables.
          variableMap.getOrElse(variables.head.group(1), null)
        } else {

          variables.map(_.group(1)).foldLeft(s)((left, variable) => {
            if (StringUtils.isBlank(variable)) {
              left
            } else {
              variableMap.get(variable) match {
                case None => ""
                case Some(value) => left.replace("${" + variable + "}", value.toString())
              }
            }
          })
        }
      }
      case _ => value
    }
  }

  val variableConversionTuple = (entry: (Any, Any)) => {
    (entry._1, variableConversion(entry._2))
  }

}
