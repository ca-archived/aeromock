package ameba.aeromock.template.freemarker

import ameba.aeromock.core.bootstrap.Bootstrap
import freemarker.log.Logger

class FreemarkerBootstrap extends Bootstrap {

  val SIMPLE_DATE = "freemarker.template.SimpleDate"

  override def process(): Unit = {

    Logger.selectLoggerLibrary(Logger.LIBRARY_NONE)

    val simpleDateCtClass = pool.get(SIMPLE_DATE)
    if (!simpleDateCtClass.isFrozen()) {
      val getAsDateMethod = simpleDateCtClass.getDeclaredMethod("getAsDate")
      getAsDateMethod.insertAfter("return ameba.aeromock.template.freemarker.BuiltinMockVariableHelper.getMockCurrentTime(date);");
      // load changed class.
      simpleDateCtClass.toClass()
    }
  }
}
