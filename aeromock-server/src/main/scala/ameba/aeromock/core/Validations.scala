package ameba.aeromock.core

import scala.reflect.ClassTag
import scalaz.Scalaz._
import scalaz.Validation
import scalaz.Validation._
import org.apache.commons.lang3.StringUtils

object Validations {

  def cast[S: ClassTag](value: Any): Validation[Throwable, S] = {
    val t = implicitly[ClassTag[S]].runtimeClass.asInstanceOf[Class[S]]
    fromTryCatch(t.cast(value))
  }
  
  def blank(value: String): Validation[Throwable, String] = {
    
    if (StringUtils.isBlank(value)) {
      new IllegalArgumentException("must be not blank").failure[String]
    } else {
      value.success[Throwable]
    }
  }
  
}