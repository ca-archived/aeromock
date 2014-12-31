package jp.co.cyberagent.aeromock.config.definition

import java.nio.file.Path

import jp.co.cyberagent.aeromock.config
import jp.co.cyberagent.aeromock.config.Tag
import jp.co.cyberagent.aeromock.config._
import jp.co.cyberagent.aeromock.template.TemplateContexts._
import jp.co.cyberagent.aeromock.template.TemplateService
import org.apache.commons.lang3.StringUtils
import scaldi.{Injectable, Injector}

import scala.collection.JavaConverters._

import scala.beans.BeanProperty
import scalaz.Scalaz._
import scalaz._

import jp.co.cyberagent.aeromock.helper._

// root element
class ProjectDef extends AnyRef with Injectable {

  @BeanProperty var template: TemplateDef = null
  @BeanProperty var data: DataDef = null
  @BeanProperty var static: StaticDef = null
  @BeanProperty var ajax: AjaxDef = null
  @BeanProperty var tag: TagDef = null
  @BeanProperty var function: FunctionDef = null
  @BeanProperty var naming: NamingDef = null
  @BeanProperty var test: TestDef = null
  @BeanProperty var protobuf: ProtoBufDef = null
  @BeanProperty var messagepack: MessagepackDef = null

  def toValue(projectConfig: Path)(implicit inj: Injector): Project = {
    val projectRoot = projectConfig.getParent

    val templateVal = Option(template) match {
      case Some(value) => template.toValue(projectRoot)
      case None => none[Template].successNel[String]
    }

    val dataVal = Option(data) match {
      case Some(value) => data.toValue(projectRoot)
      case None => none[Data].successNel[String]
    }

    val staticVal = Option(static) match {
      case Some(value) => static.toValue(projectRoot)
      case None => none[Static].successNel[String]
    }

    val ajaxVal = Option(ajax) match {
      case Some(value) => ajax.toValue(projectRoot)
      case None => none[Ajax].successNel[String]
    }

    val tagVal = Option(tag) match {
      case Some(value) => tag.toValue(projectRoot)
      case None => none[Tag].successNel[String]
    }

    val functionVal = Option(function) match {
      case Some(value) => function.toValue(projectRoot)
      case None => none[Function].successNel[String]
    }

    val namingVal = Option(naming) match {
      case Some(value) => naming.toValue
      case None => Naming().successNel[String]
    }

    val testVal = Option(test) match {
      case Some(value) => test.toValue(projectRoot)
      case None => Test(projectRoot / "aeromock_report").successNel[String]
    }

    val protobufVal = Option(protobuf) match {
      case Some(value) => protobuf.toValue(projectRoot)
      case None => none[ProtoBuf].successNel[String]
    }

    val messagepackVal = Option(messagepack) match {
      case Some(value) => messagepack.toValue(projectRoot)
      case None => none[Messagepack].successNel[String]
    }

    Project(
      projectConfig,
      projectRoot,
      templateVal,
      dataVal,
      staticVal,
      ajaxVal,
      tagVal,
      functionVal,
      namingVal,
      testVal,
      protobufVal,
      messagepackVal)

  }
}

// project.yaml -> template
class TemplateDef extends AnyRef with Injectable {
  // template -> root
  @BeanProperty var root: String = null
  // template -> serviceClass
  @BeanProperty var serviceClass: String = null
  // template -> contexts
  @BeanProperty var contexts: java.util.List[TemplateContextDef] = null

  def toValue(projectRoot: Path)(implicit inj:Injector): ValidationNel[String, Option[Template]] = {

    val rootResult = root match {
      case null => message"validation.need.element${"root"}${"template"}".failureNel[Path]
      case s if StringUtils.isBlank(s) => message"validation.not.blank${"template.root"}".failureNel[Path]
      case _ => {

        val rootPath = projectRoot / root
        if (!rootPath.exists) {
          message"validation.not.exists.path${rootPath}${"template.root"}".failureNel[Path]
        } else if (!rootPath.isDirectory) {
          message"validation.not.directory${rootPath}${"template.root"}".failureNel[Path]
        } else {
          rootPath.successNel
        }
      }
    }

    type TemplateClass = Class[_ <: TemplateService]

    val serviceClassResult = serviceClass match {
      case null => message"validation.need.element${"serviceClass"}${"template"}".failureNel[TemplateClass]
      case c if StringUtils.isBlank(c) => message"validation.not.blank${"template.serviceClass"}".failureNel[TemplateClass]
      case _ => {
        val targetClass = serviceClass.replace("ameba.aeromock", "jp.co.cyberagent.aeromock")
        try {
          val clazz = Class.forName(targetClass)
          val parent = classOf[TemplateService]
          if (!parent.isAssignableFrom(clazz)) {
            message"validation.not.subclass${targetClass}${parent.getName()}${"template.serviceClass"}".failureNel[TemplateClass]
          } else {
            clazz.asInstanceOf[TemplateClass].successNel
          }
        } catch {
          case e: ClassNotFoundException => {
            message"validation.not.exists.class${targetClass}${"template.serviceClass"}".failureNel[TemplateClass]
          }
          case e: Throwable => {
            message"validation.fail.load.class${targetClass}${"template.serviceClass"}".failureNel[TemplateClass]
          }
        }
      }
    }

    val contextsResult = rootResult match {
      case Success(value) => {
        Option(contexts) match {
          case Some(n) => {
            val values = contexts.asScala.toSeq.map(_.toValue(value))
            val naviList = values.collect {
              case Success(navi) => navi
            }
            val errors = (values.collect {
              case Failure(value) => value.list
            }).foldLeft(List[String]())((left, right) => left ++ right)

            if (errors.isEmpty) {
              naviList.toList.successNel[String]
            } else {
              errors.mkString(", ").failureNel[List[TemplateContext]]
            }
          }
          case None => List.empty.successNel
        }

      }
      case Failure(value) => {
        // navigatorがtemplate.rootの値に依存するので
        List[TemplateContext]().successNel[String]
      }
    }

    (rootResult |@| serviceClassResult |@| contextsResult) {
      Template(_, _, _).some
    }
  }

}

class TemplateContextDef extends AnyRef with Injectable {
  // contexts[] -> domain
  @BeanProperty var domain: String = null
  // contexts[] -> root
  @BeanProperty var root: String = null

  def toValue(templateRootPath: Path)(implicit inj: Injector): ValidationNel[String, TemplateContext] = {

    val domainPattern = """^[0-9a-zA-Z\.]+$""".r

    val domainResult = domain match {
      case null => message"validation.need.element${"domain"}${"template.contexts[]"}".failureNel[String]
      case s if StringUtils.isBlank(s) => message"validation.not.blank${"template.contexts[].domain"}".failureNel[String]
      case s if domainPattern.pattern.matcher(s).matches() => domain.successNel
      case _ => "navigator.domain must be domain format.".failureNel[String]
    }

    val navigatorRootPathResult = root match {
      case null => message"validation.need.element${"root"}${"template.contexts[]"}".failureNel[Path]
      case s if StringUtils.isBlank(s) => message"validation.not.blank${"template.contexts[].root"}".failureNel[Path]
      case _ => {

        val rootPath = templateRootPath / root
        if (!rootPath.exists) {
          message"validation.not.exists.path${rootPath}${"template.contexts[].root"}".failureNel[Path]
        } else if (!rootPath.isDirectory) {
          message"validation.not.directory${rootPath}${"template.contexts[].root"}".failureNel[Path]
        } else {
          (templateRootPath / root).successNel
        }
      }
    }

    (domainResult |@| navigatorRootPathResult) {
      TemplateContext(_, inject[Int](identified by 'listenPort), _)
    }
  }

}

// project.yaml -> data
class DataDef {
  // data -> root
  @BeanProperty var root: String = null

  def toValue(projectRoot: Path): ValidationNel[String, Option[Data]] = {

    val rootResult = root match {
      case null => message"validation.need.element${"root"}${"data"}".failureNel[Path]
      case s if StringUtils.isBlank(s) => message"validation.not.blank${"data.root"}".failureNel[Path]
      case _ => {

        val rootPath = projectRoot / root
        if (!rootPath.exists) {
          message"validation.not.exists.path${rootPath}${"data.root"}".failureNel[Path]
        } else if (!rootPath.isDirectory) {
          message"validation.not.directory${rootPath}${"data.root"}".failureNel[Path]
        } else {
          rootPath.successNel
        }
      }
    }

    for {
      dataRoot <- rootResult
    } yield (Data(dataRoot).some)
  }
}

// project.yaml -> static
class StaticDef {
  // static -> root
  @BeanProperty var root: String = null

  def toValue(projectRoot: Path): ValidationNel[String, Option[Static]] = {
    root match {
      case null => message"validation.need.element${"root"}${"static"}".failureNel[Option[Static]]
      case s if StringUtils.isBlank(s) => message"validation.not.blank${"static.root"}".failureNel[Option[Static]]
      case _ => {

        val rootPath = projectRoot / root
        if (!rootPath.exists) {
          message"validation.not.exists.path${rootPath}${"static.root"}".failureNel[Option[Static]]
        } else if (!rootPath.isDirectory) {
          message"validation.not.directory${rootPath}${"static.root"}".failureNel[Option[Static]]
        } else {
          Static(rootPath).some.successNel
        }
      }
    }
  }
}

// project.yaml -> ajax
class AjaxDef {
  // ajax -> root
  @BeanProperty var root: String = null

  def toValue(projectRoot: Path): ValidationNel[String, Option[Ajax]] = {

    val rootResult = root match {
      case null => message"validation.need.element${"root"}${"ajax"}".failureNel[Path]
      case s if StringUtils.isBlank(s) => message"validation.not.blank${"ajax.root"}".failureNel[Path]
      case _ => {

        val rootPath = projectRoot / root
        if (!rootPath.exists) {
          message"validation.not.exists.path${rootPath}${"ajax.root"}".failureNel[Path]
        } else if (!rootPath.isDirectory) {
          message"validation.not.directory${rootPath}${"ajax.root"}".failureNel[Path]
        } else {
          rootPath.successNel
        }
      }
    }

    for {
      ajaxRoot <- rootResult
    } yield (Ajax(ajaxRoot).some)
  }

}

// project.yaml -> tag
class TagDef {
  // tag -> root
  @BeanProperty var root: String = null

  def toValue(projectRoot: Path): ValidationNel[String, Option[Tag]] = {

    root match {
      case null => message"validation.need.element${"root"}${"tag"}".failureNel[Option[Tag]]
      case s if StringUtils.isBlank(s) => message"validation.not.blank${"tag.root"}".failureNel[Option[Tag]]
      case _ => {

        val rootPath = projectRoot / root
        if (!rootPath.exists) {
          message"validation.not.exists.path${rootPath}${"tag.root"}".failureNel[Option[Tag]]
        } else if (!rootPath.isDirectory) {
          message"validation.not.directory${rootPath}${"tag.root"}".failureNel[Option[Tag]]
        } else {
          config.Tag(rootPath).some.successNel
        }
      }
    }
  }
}

// project.yaml -> function
class FunctionDef {
  // function -> root
  @BeanProperty var root: String = null

  def toValue(projectRoot: Path): ValidationNel[String, Option[Function]] = {

    root match {
      case null => message"validation.need.element${"root"}${"function"}".failureNel[Option[Function]]
      case s if StringUtils.isBlank(s) => message"validation.not.blank${"function.root"}".failureNel[Option[Function]]
      case _ => {

        val rootPath = projectRoot / root
        if (!rootPath.exists) {
          message"validation.not.exists.path${rootPath}${"function.root"}".failureNel[Option[Function]]
        } else if (!rootPath.isDirectory) {
          message"validation.not.directory${rootPath}${"function.root"}".failureNel[Option[Function]]
        } else {
          Function(rootPath).some.successNel
        }
      }
    }
  }
}

// project.yaml -> naming
class NamingDef {
  // naming -> dataPrefix
  @BeanProperty var dataPrefix: String = null
  // naming -> dataidQuery
  @BeanProperty var dataidQuery: String = null

  def toValue(): ValidationNel[String, Naming] = {
    val dataPrefixResult = dataPrefix match {
      case null => "__".successNel[String]
      case s if StringUtils.isBlank(s) => "__".successNel[String]
      case _ => dataPrefix.trim().successNel[String]
    }

    val dataidQueryResult = dataidQuery match {
      case null => "_dataid".successNel[String]
      case s if StringUtils.isBlank(s) => "_dataid".successNel[String]
      case _ => dataidQuery.trim().successNel[String]
    }

    (dataPrefixResult |@| dataidQueryResult) apply Naming
  }
}

// project.yaml -> test
class TestDef {
  // test -> reportRoot
  @BeanProperty var reportRoot: String = null

  def toValue(projectRoot: Path): ValidationNel[String, Test] = {
    reportRoot match {
      case null => Test(projectRoot / "aeromock_report").successNel[String]
      case s if StringUtils.isBlank(s) => Test(projectRoot / "aeromock_report").successNel[String]
      case _ => Test(projectRoot / reportRoot).successNel[String]
    }
  }
}

// project.yaml -> protobuf
class ProtoBufDef {
  // protobuf
  @BeanProperty var root: String = null
  @BeanProperty var apiPrefix: String = null

  def toValue(projectRoot: Path): ValidationNel[String, Option[ProtoBuf]] = {

    val rootResult = root match {
      case null => message"validation.need.element${"root"}${"protobuf"}".failureNel[Path]
      case s if StringUtils.isBlank(s) => message"validation.not.blank${"protobuf.root"}".failureNel[Path]
      case _ => {

        val rootPath = projectRoot / root
        if (!rootPath.exists) {
          message"validation.not.exists.path${rootPath}${"protobuf.root"}".failureNel[Path]
        } else if (!rootPath.isDirectory) {
          message"validation.not.directory${rootPath}${"protobuf.root"}".failureNel[Path]
        } else {
          rootPath.successNel
        }
      }
    }

    val apiPrefixResult = Option(apiPrefix) match {
      case None => none[String].successNel[String]
      case Some(s) if StringUtils.isNotBlank(s) => s.some.successNel[String]
    }

    for {
      protobufRoot <- rootResult
      protobufApiPrefix <- apiPrefixResult
    } yield (ProtoBuf(protobufRoot, protobufApiPrefix).some)
  }
}

// project.yaml -> messagepack
class MessagepackDef {
  // messagepack
  @BeanProperty var root: String = null

  def toValue(projectRoot: Path): ValidationNel[String, Option[Messagepack]] = {
    root match {
      case null => message"validation.need.element${"root"}${"messagepack"}".failureNel[Option[Messagepack]]
      case s if StringUtils.isBlank(s) => message"validation.not.blank${"messagepack.root"}".failureNel[Option[Messagepack]]
      case _ => {

        val rootPath = projectRoot / root
        if (!rootPath.exists) {
          message"validation.not.exists.path${rootPath}${"messagepack.root"}".failureNel[Option[Messagepack]]
        } else if (!rootPath.isDirectory) {
          message"validation.not.directory${rootPath}${"messagepack.root"}".failureNel[Option[Messagepack]]
        } else {
          Messagepack(rootPath).some.successNel
        }
      }
    }
  }
}
