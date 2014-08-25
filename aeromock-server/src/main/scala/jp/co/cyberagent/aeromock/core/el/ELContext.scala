package jp.co.cyberagent.aeromock.core.el

import javax.el.ELProcessor

import jp.co.cyberagent.aeromock.AeromockELException
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.helper.DeepTraversal._
import org.apache.commons.lang3.StringUtils


/**
 * EL(JSR-341) context object.
 * @author stormcat24
 */
class ELContext(values: Map[String, Any] = Map.empty) {

  val EL_PATTERN = """\$\{([^\$]+)\}""".r
  val nop = (a: Any) => a

  /**
   * execute EL expression.
   * @param expression EL
   * @return return value of expression
   */
  def eval(expression: String): AnyRef = {

    if (StringUtils.isBlank(expression)) {
      return expression
    }

    val expressions = EL_PATTERN.findAllMatchIn(expression).toList

    if (expressions.isEmpty) {
      return expression
    }

    val el = new ELProcessor with ELProcessorMixin
    values.map {
      case (key, value: Map[_, _]) => el.defineBean(key, asJavaMap(value)(nop))
      case (key, value: Seq[_]) => el.defineBean(key, asJavaCollection(value)(nop))
      case (key, value) => el.defineBean(key, value)
    }

    if (expressions.size == 1 && expressions.head.matched == expression) {
      el.eval(expressions.head.group(1))
    } else {
      expressions.foldLeft(expression)((left, reg) => {
        if (StringUtils.isBlank(left)) left else left.replace(reg.matched, el.eval(reg.group(1)).toString)
      })
    }

  }
}

trait ELProcessorMixin extends ELProcessor {

  override def eval(expression: String): AnyRef = {
    trye(super.eval(expression)) match {
      case Right(value) => value
      case Left(e) => throw new AeromockELException(expression, e)
    }
  }

}
