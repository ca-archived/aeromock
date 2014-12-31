package jp.co.cyberagent.aeromock.msgpack

import java.math.BigInteger

import org.msgpack.`type`.{ValueFactory, Value}

/**
 *
 * @author stormcat24
 */
object MessagepackValue {

  def fromIterable(data: Iterable[_]): Value = {
    data match {
      case m: Map[Any, Any] @unchecked => ValueFactory.createArrayValue(m.map(e => createValue(e._2)).toArray)
      case l: Seq[Any] @unchecked => ValueFactory.createArrayValue(l.map(createValue(_)).toArray)
    }
  }

  private def createValue(v: Any): Value = {
    v match {
      case null => ValueFactory.createNilValue
      case v: Map[Any, Any] => fromIterable(v)
      case v: Seq[Any] => fromIterable(v)
      case v: CharSequence => ValueFactory.createRawValue(v.toString)
      case v: Boolean => ValueFactory.createBooleanValue(v)
      case v: Int => ValueFactory.createIntegerValue(v)
      case v: Byte => ValueFactory.createIntegerValue(v)
      case v: Short => ValueFactory.createIntegerValue(v)
      case v: Long => ValueFactory.createIntegerValue(v)
      case v: BigInteger => ValueFactory.createIntegerValue(v)
      case v: Float => ValueFactory.createFloatValue(v)
      case v: Double => ValueFactory.createFloatValue(v)
    }
  }

}
