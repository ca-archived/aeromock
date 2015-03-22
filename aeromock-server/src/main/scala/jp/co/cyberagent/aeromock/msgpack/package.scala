package jp.co.cyberagent.aeromock

import java.math.BigInteger

import org.msgpack.`type`.{ValueFactory, Value}

/**
 *
 * @author stormcat24
 */
package object msgpack {

  trait MessagepackValue {

    def fromIterable(data: Iterable[_]): Value

    protected def createValue(v: Any): Value = {
      v match {
        case null => ValueFactory.createNilValue
        case v: Map[Any, Any] @unchecked => fromIterable(v)
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


  object CompactMessagepackValue extends MessagepackValue {

    override def fromIterable(data: Iterable[_]): Value = {
      data match {
        case m: Map[Any, Any] @unchecked => ValueFactory.createArrayValue(m.map(e => createValue(e._2)).toArray)
        case l: Seq[Any] @unchecked => ValueFactory.createArrayValue(l.map(createValue(_)).toArray)
      }
    }
  }

  object JsonMessagepackValue extends MessagepackValue {

    override def fromIterable(data: Iterable[_]): Value = {
      data match {
        case m: Map[Any, Any] @unchecked => {
          val result = m.flatMap { e =>
            Array(createValue(e._1), createValue(e._2))
          }.toArray
          ValueFactory.createMapValue(result)
        }
        case l: Seq[Any] @unchecked => ValueFactory.createArrayValue(l.map(createValue(_)).toArray)
      }
    }
  }


  object MessagepackValue {

    def apply(mode: String): MessagepackValue = {
      Option(mode) match {
        case Some("compact") => CompactMessagepackValue
        case _ => JsonMessagepackValue
      }
    }

  }

}
