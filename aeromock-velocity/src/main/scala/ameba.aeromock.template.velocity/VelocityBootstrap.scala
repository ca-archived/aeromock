package ameba.aeromock.template.velocity

import ameba.aeromock.core.bootstrap.Bootstrap

/**
 * Bootstrap of Velocity
 * @author stormcat24
 */
class VelocityBootstrap extends Bootstrap {

  val RUNTIME_INSTANCE = "org.apache.velocity.runtime.RuntimeInstance"

  /**
   * @inheritdoc
   */
  override def process(): Unit = {
    val runtimeInstanceCtClass = pool.get(RUNTIME_INSTANCE)
    if (!runtimeInstanceCtClass.isFrozen) {
      val initMethod = runtimeInstanceCtClass.getDeclaredMethod("init")

      // [note] assign 'false' to 'initialized' forcely to reload configuration.
      initMethod.insertBefore("""initialized = false;""");
      runtimeInstanceCtClass.toClass
    }
  }
}
