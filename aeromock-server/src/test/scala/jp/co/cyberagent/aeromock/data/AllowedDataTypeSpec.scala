package jp.co.cyberagent.aeromock.data

import org.specs2.mutable.{Tables, Specification}

/**
 *
 * @author stormcat24
 */
class AllowedDataTypeSpec extends Specification with Tables {

  "AllowedDataType" should {

    "extensions" in {

      AllowedDataType.extensions must contain(allOf("json", "yaml", "yml"))
    }

  }

}
