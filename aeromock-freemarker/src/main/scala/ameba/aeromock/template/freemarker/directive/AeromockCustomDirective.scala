package ameba.aeromock.template.freemarker.directive

import freemarker.template.TemplateModel

/**
 * Trait of dynamic custom directive.
 * @author stormcat24
 *
 */
trait AeromockCustomDirective extends TemplateModel {

  def getDirevtiveName(): String

}
