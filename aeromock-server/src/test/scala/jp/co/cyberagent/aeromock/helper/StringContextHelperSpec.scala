package jp.co.cyberagent.aeromock.helper

import java.util.Locale

import jp.co.cyberagent.aeromock.SpecSupport
import org.specs2.mutable.{Tables, Specification}

/**
 *
 * @author stormcat24
 */
class StringContextHelperSpec extends Specification with Tables with SpecSupport {

  Locale.setDefault(Locale.US)
  "none placeholder message" in {
    message"test.none.placeholder" must_== "None Placeholder"
  }

  "has placeholder message" in {
    "without arguments" in {
      message"test.has.placeholder" must_== "Has Placeholder {0}, {1}."
    }
    "with arguments, sequentially" in {
      message"test.has.placeholder${"arg1"}${"arg2"}" must_== "Has Placeholder arg1, arg2."
    }
    "with arguments, as varargs" in {
      message"test.has.placeholder${Seq("arg1", "arg2"): _*}" must_== "Has Placeholder arg1, arg2."
    }
  }
}
