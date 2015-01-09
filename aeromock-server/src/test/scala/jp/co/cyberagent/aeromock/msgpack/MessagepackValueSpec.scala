package jp.co.cyberagent.aeromock.msgpack

import java.math.BigInteger

import jp.co.cyberagent.aeromock.test.SpecSupport
import org.msgpack.`type`.ValueFactory
import org.specs2.mutable.{Tables, Specification}

/**
 *
 * @author stormcat24
 */
class MessagepackValueSpec extends Specification with Tables with SpecSupport {

  "array type" should {
    "array simple" in {
      val expected = ValueFactory.createArrayValue(Array(
        ValueFactory.createIntegerValue(100),
        ValueFactory.createIntegerValue(200),
        ValueFactory.createIntegerValue(300)
      ))
      val input = List(100, 200, 300)
      MessagepackValue("compact").fromIterable(input) must_== expected
    }

    "array complex" in {
      val expected = ValueFactory.createArrayValue(Array(
        ValueFactory.createIntegerValue(100),
        ValueFactory.createFloatValue(20.0f),
        ValueFactory.createFloatValue(30.0),
        ValueFactory.createBooleanValue(true),
        ValueFactory.createIntegerValue(400.toShort),
        ValueFactory.createIntegerValue(500L),
        ValueFactory.createIntegerValue(BigInteger.valueOf(600L)),
        ValueFactory.createNilValue,
        ValueFactory.createRawValue("700")
      ))
      val input = List(100, 20.0f, 30.0, true, 400.toShort, 500L, BigInteger.valueOf(600L), null, "700")
      MessagepackValue("compact").fromIterable(input) must_== expected
    }
  }

  "map type" should {
    "simple" in {
      val expected = ValueFactory.createArrayValue(Array(
        ValueFactory.createIntegerValue(100),
        ValueFactory.createArrayValue(Array(
          ValueFactory.createIntegerValue(1000),
          ValueFactory.createRawValue("prop1mapValue")
        ))
      ))

      val input = Map(
        "id" -> 100,
        "detail" -> Map(
          "intprop" -> 1000,
          "stringprop" -> "prop1mapValue"
        )
      )
      MessagepackValue("compact").fromIterable(input) must_== expected
    }

    "complex" in {
      val expected = ValueFactory.createArrayValue(Array(
        ValueFactory.createIntegerValue(100),
        ValueFactory.createArrayValue(Array(
          ValueFactory.createIntegerValue(100),
          ValueFactory.createIntegerValue(200),
          ValueFactory.createIntegerValue(300)
        )),
        ValueFactory.createArrayValue(Array(
          ValueFactory.createArrayValue(Array(
            ValueFactory.createRawValue("prop1value")
          )),
          ValueFactory.createArrayValue(Array(
            ValueFactory.createRawValue("prop2value")
          ))
        )),
        ValueFactory.createArrayValue(Array(
          ValueFactory.createRawValue("prop1value"),
          ValueFactory.createArrayValue(Array(
            ValueFactory.createIntegerValue(10),
            ValueFactory.createIntegerValue(20),
            ValueFactory.createIntegerValue(30)
          ))
        ))
      ))

      val input = Map(
        "id" -> 100,
        "array" -> List(
          100, 200, 300
        ),
        "arraymap" -> List(
          Map("prop1" -> "prop1value"),
          Map("prop2" -> "prop2value")
        ),
        "map" -> Map(
          "prop1" -> "prop1value",
          "array" -> List(10, 20, 30)
        )
      )

      MessagepackValue("compact").fromIterable(input) must_== expected
    }
  }
}
