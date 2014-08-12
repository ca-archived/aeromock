package ameba.aeromock.data

import scalaz._
import scalaz.Scalaz._
import scalaz.Validation._
import scala.collection.JavaConverters._
import ameba.aeromock.proxy.DynamicCollectionProxyBuilder
import ameba.aeromock.proxy.DynamicHashProxyJavaBuilder
import ameba.aeromock.proxy.ProxyParameter
import ameba.aeromock.config.entity.Naming
import ameba.aeromock.helper._

trait InstanceDef

/**
 * Class to express projection of instance.
 * @author stormcat24
 */
case class InstanceProjection(
  decoration: (Any => Any),
  properties: List[PropertyDef],
  methods: List[MethodDef],
  externalList: Option[ListPropertyValue] = None,
  jsonObject: Boolean = false) extends InstanceDef with MethodReturnValue {
  def hasProperty: Boolean = !properties.isEmpty
  def hasMethod: Boolean = !methods.isEmpty
  def needsProxy: Boolean = hasMethod || externalList.isDefined

  // TODO for Scala template.
  def toInstance: Any = ???

  /**
   * Create instance from structure.
   * @return instance
   */
  def toInstanceJava(): Any = {

    if (externalList.isDefined) {
      new DynamicCollectionProxyBuilder(this).build(Array(new ProxyParameter(jsonObject)))
    } else if (hasMethod || jsonObject) {
      new DynamicHashProxyJavaBuilder(this).build(Array(new ProxyParameter(jsonObject)))
    } else {
      val map = new java.util.LinkedHashMap[Any, Any]
      properties.foreach(property => {
        map.put(property.key.key, decoration(processPropertyJava(property.value)))
      })
      map
    }
  }

  def processPropertyJava(any: Any): Any = {
    any match {
      case SimplePropertyValue(value) => value
      case ListPropertyValue(list) => list.map(e => decoration(processPropertyJava(e))).asJava
      case child: InstanceProjection => child.toInstanceJava
    }
  }

}

case class MethodDef(name: String, value: Any)
trait MethodReturnValue

case class PropertyDef(key: PropertyKey, value: InstanceDef)
case class PropertyKey(key: Any)
case class SimplePropertyValue(value: Any) extends InstanceDef
case class ListPropertyValue(value: List[_]) extends InstanceDef

/**
 * Factory to create [[ameba.aeromock.data.InstanceProjection]].
 * @author stormcat24
 */
class InstanceProjectionFactory(decoration: (Any => Any), naming: Naming) {

  def create(map: Map[Any, Any]): ValidationNel[Throwable, InstanceProjection] = {

    val methods = createMethods(map)
    val list = createExternalList(map)
    val jsonObject = createJson(map)
    val properties = (map.collect {
      case p if (p._1 != naming.methods && p._1 != naming.list && p._1 != naming.json) => toPropertyDef(p)
    }).toList

    (properties.sequenceU |@| methods |@| list |@| jsonObject) { InstanceProjection(decoration, _, _, _, _)}
  }

  // create __methods
  def createMethods(target: Map[Any, Any]): ValidationNel[Throwable, List[MethodDef]] = {
    target.get(naming.methods) match {
      case None => List[MethodDef]().successNel[Throwable]
      case Some(__methods) => {
        val methods = cast[List[Any]](__methods).toValidationNel
        methods.flatMap(_.map(toMethodDef(_)).sequenceU)
      }
    }

  }

  // create __list
  def createExternalList(target: Map[Any, Any]): ValidationNel[Throwable, Option[ListPropertyValue]] = {
    target.get(naming.list) match {
      case None => none[ListPropertyValue].successNel[Throwable]
      case Some(value) => {
        val __list = cast[List[Any]](value).toValidationNel
        __list match {
          case Failure(f) => f.failure[Option[ListPropertyValue]]
          case Success(list) => {
            list.map(toValue(_)).sequenceU match {
              case Failure(f) => f.failure[Option[ListPropertyValue]]
              case Success(list) => ListPropertyValue(list).some.successNel[Throwable]
            }
          }
        }
      }
    }
  }

  // __json
  def createJson(target: Map[Any, Any]): ValidationNel[Throwable, Boolean] = {
    target.get(naming.json) match {
      case None => false.successNel[Throwable]
      case Some(__json) => {
        fromTryCatch(__json.asInstanceOf[Boolean]).toValidationNel
      }
    }
  }

  def toMethodDef(target: Any): ValidationNel[Throwable, MethodDef] = {
    val map = cast[Map[Any, Any]](target).toValidationNel

    map.flatMap { m =>

      val nameResult = for {
        // TODO Exception
        nameRaw <- m.get("name").toSuccess(new Throwable("'name' not specified"))
        name <- nameRaw |> parseKey
      } yield (name)

      val valueResult = (for {
        value <- m.get("value").toSuccess(new Throwable("'value' not specfied"))
      } yield (value)) match {
        case Failure(f) => f.failNel[InstanceDef]
        case Success(s) => toValue(s)
      }
      (nameResult.toValidationNel |@| valueResult) apply MethodDef
    }
  }

  def toListProperty(value: List[_]): ValidationNel[Throwable, ListPropertyValue] = {

    value.map(toValue(_)).sequenceU match {
      case Failure(f) => f.failure[ListPropertyValue]
      case Success(elements) => ListPropertyValue(elements).successNel[Throwable]
    }
  }

  def toValue(value: Any): ValidationNel[Throwable, InstanceDef] = {
    value match {
      case map: Map[Any, Any] @unchecked => create(map)
      case list: List[_] => toListProperty(list)
      case v => toPropertyValue(v)
    }
  }

  private def toPropertyDef(property: (Any, Any)): ValidationNel[Throwable, PropertyDef] = {
    property match {
      case (key, map: Map[Any, Any] @unchecked) => {
        (toPropertyKey(key) |@| create(map)) apply PropertyDef
      }
      case (key, list: List[_]) => {
        (toPropertyKey(key) |@| toListProperty(list)) apply PropertyDef
      }
      case (key, value) => {
        (toPropertyKey(key) |@| toPropertyValue(value)) apply PropertyDef
      }
    }
  }

  private def toPropertyKey(key: Any): ValidationNel[Throwable, PropertyKey] = {
    PropertyKey(key).successNel[Throwable]
  }

  private def toPropertyValue(value: Any): ValidationNel[Throwable, InstanceDef] = {
    SimplePropertyValue(value).asInstanceOf[InstanceDef].successNel[Throwable]
  }

  def parseKey(value: Any): Validation[Throwable, String] = fromTryCatch(value.toString())
  def parseObject(value: Any): Validation[Throwable, Any] = fromTryCatch(value)

}
