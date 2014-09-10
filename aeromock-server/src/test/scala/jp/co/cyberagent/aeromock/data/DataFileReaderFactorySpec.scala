package jp.co.cyberagent.aeromock.data

import java.nio.file.Paths

import jp.co.cyberagent.aeromock.SpecSupport
import org.specs2.mutable.{Tables, Specification}
import DataFileReaderFactory._

/**
 *
 * @author stormcat24
 */
class DataFileReaderFactorySpec extends Specification with Tables with SpecSupport {

  "create" should {
    "Illegal values" in {
      create(null) must beNone
      create(Paths.get("not_exists")) must beNone
    }

    "exists files" in {

      "path" | "expect" |
      "data.json" ! classOf[JsonDataFileReader] |
      "data.yml"  ! classOf[YamlDataFileReader] |
      "data.yaml" ! classOf[YamlDataFileReader] |> { (path, expect) =>
        create(getResourcePath(s"data/DataFileReaderFactory/$path")).map(_.getClass) must_== Some(expect)
      }
    }

  }
}
