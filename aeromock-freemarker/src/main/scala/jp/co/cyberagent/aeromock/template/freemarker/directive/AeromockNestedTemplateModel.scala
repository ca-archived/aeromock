package jp.co.cyberagent.aeromock.template.freemarker.directive

import freemarker.template.SimpleHash

class AeromockNestedTemplateModel(directiveName: String) extends SimpleHash with AeromockCustomDirective {

  override def getDirevtiveName(): String = directiveName
}
