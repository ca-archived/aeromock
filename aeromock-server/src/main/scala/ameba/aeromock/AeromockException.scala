package ameba.aeromock

import java.nio.file.Path

import ameba.aeromock.config.MessageManager
import io.netty.handler.codec.http.HttpMethod

import scalaz.NonEmptyList


sealed abstract class AeromockException(message: String, cause: Throwable) extends RuntimeException(message, cause) {
  def this(msg: String) = this(msg, null)
  def this(`class`: Class[_ <: AeromockException], cause: Throwable, args: AnyRef*) = this(MessageManager.getMessage(`class`, args:_*), cause)
  def this(key: String, cause: Throwable, args: AnyRef*) = this(MessageManager.getMessage(key, args:_*), cause)
}

// unexpected error
class AeromockSystemException(message: String, cause: Throwable) extends AeromockException(message, cause) {
  def this(msg: String) = this(msg, null)
  def this(`class`: Class[_ <: AeromockException], cause: Throwable, args: AnyRef*) = this(MessageManager.getMessage(`class`, args:_*), cause)
  def this(key: String, cause: Throwable, args: AnyRef*) = this(MessageManager.getMessage(key, args:_*), cause)
}

// BadUsing START
class AeromockBadUsingException(message: String, cause: Throwable) extends AeromockException(message, cause) {
  def this(msg: String) = this(msg, null)
  def this(`class`: Class[_ <: AeromockException], cause: Throwable, args: AnyRef*) = this(MessageManager.getMessage(`class`, args:_*), cause)
  def this(key: String, cause: Throwable, args: AnyRef*) = this(MessageManager.getMessage(key, args:_*), cause)
}

class AeromockApiBadRequestException(uri: String, cause: Throwable)
  extends AeromockBadUsingException(classOf[AeromockApiBadRequestException], cause, uri) {
  def this(uri: String) = this(uri, null)
}

class AeromockInvalidRequestException(uri: String, cause: Throwable)
  extends AeromockBadUsingException(classOf[AeromockInvalidRequestException], cause, uri) {
  def this(uri: String) = this(uri, null)
}
// BadUsing END

// Configuration START
class AeromockConfigurationException(val config: Path, val errors: NonEmptyList[String]) extends AeromockException(errors.list.mkString(",")) {
  def this(config: Path, message: String) = this(config, NonEmptyList(message))
}
// Configuration END

// NotFound START
sealed abstract class AeromockNotFoundException(resource: String, cause: Throwable) extends AeromockException(resource, cause) {
  def this(resource: String) = this(resource, null)
  def this(`class`: Class[_ <: AeromockException], cause: Throwable, args: AnyRef*) = this(MessageManager.getMessage(`class`, args:_*), cause)
  def this(key: String, cause: Throwable, args: AnyRef*) = this(MessageManager.getMessage(key, cause, args), cause)
}

class AeromockResourceNotFoundException(path: String, cause: Throwable)
  extends AeromockNotFoundException(classOf[AeromockResourceNotFoundException], cause, path) {
  def this(path: String) = this(path, null)
}

class AeromockTemplateNotFoundException(templatePath: String, cause: Throwable)
  extends AeromockNotFoundException(classOf[AeromockTemplateNotFoundException], cause, templatePath) {
  def this(templatePath: String) = this(templatePath, null)
}

class AeromockApiNotFoundException(uri: String, cause: Throwable)
  extends AeromockNotFoundException(classOf[AeromockApiNotFoundException], cause, uri) {
  def this(uri: String) = this(uri, null)
}
// NotFound END

// BadImplementation START
sealed abstract class AeromockBadImplementation(path: String, cause: Throwable) extends AeromockException(path, cause){
  def this(msg: String) = this(msg, null)
  def this(`class`: Class[_ <: AeromockBadImplementation], cause: Throwable, args: AnyRef*) = this(MessageManager.getMessage(`class`, args:_*), cause)
  def this(key: String, cause: Throwable, args: AnyRef*) = this(MessageManager.getMessage(key, args:_*), cause)
}

class AeromockNoneRelatedDataException(dataPath: String, cause: Throwable)
  extends AeromockBadImplementation(classOf[AeromockNoneRelatedDataException], cause, dataPath) {
  def this(dataPath: String) = this(dataPath, null)
}

class AeromockScriptExecutionException(scriptPath: Path, cause: Throwable)
  extends AeromockBadImplementation(classOf[AeromockScriptExecutionException], cause, scriptPath) {
  def this(scriptPath: Path) = this(scriptPath, null)
}

class AeromockScriptBadReturnTypeException(returnType: Class[_], path: Path, cause: Throwable)
  extends AeromockBadImplementation(classOf[AeromockScriptBadReturnTypeException], cause, returnType, path) {
  def this(returnType: Class[_], path: Path) = this(returnType, path, null)
}

class AeromockInvalidDataFileException(dataPath: Path, cause: Throwable)
  extends AeromockBadImplementation(classOf[AeromockInvalidDataFileException], cause, dataPath) {
  def this(dataPath: Path) = this(dataPath, null)
}

class AeromockTemplateParseException(path: String, cause: Throwable)
  extends AeromockBadImplementation(classOf[AeromockTemplateParseException], cause, path) {
  def this(path: String) = this(path, null)
}

class AeromockLoadDataException(errors: NonEmptyList[String]) extends AeromockBadImplementation(errors.list.mkString(",")) {
}

class AeromockRenderException(path: String, cause: Throwable)
  extends AeromockBadImplementation(classOf[AeromockRenderException], cause, path) {
  def this(path: String) = this(path, null)
}
// BadImplementation END

// MethodNotAllowed START
class AeromockMethodNotAllowedException(method: HttpMethod, uri: String, cause: Throwable)
  extends AeromockException(classOf[AeromockMethodNotAllowedException], cause, method, uri) {
  def this(method: HttpMethod, uri: String) = this(method, uri, null)
}
// MethodNotAllowed END
