package jp.co.cyberagent.aeromock.protobuf

import java.nio.file.Path

import com.squareup.protoparser.{ProtoFile, EnumType, MessageType, ProtoSchemaParser}
import jp.co.cyberagent.aeromock.helper._
import jp.co.cyberagent.aeromock.protobuf.ProtoFieldTypes.MESSAGE

import scala.collection.JavaConverters._
import scalaz._
import Scalaz._

/**
 *
 * @author stormcat24
 */
class ProtoFileParser(protobufRoot: Path) {

  def parseProto(protoFile: Path): ValidationNel[String, ParsedProto] = {

    // TODO getExtendDeclarations
    for {
      proto <- parseProtoFile(protoFile)
      allDeps <- fetchDependencies(proto.getDependencies.asScala.toSet)
      dependencyTypes <- getDependencyTypes(allDeps)
      types <- {
        proto.getTypes.asScala.map {
          case mt: MessageType => (mt.getName, mt, proto)
        }.map(fetchType).toList.sequenceU
      }
    } yield (ParsedProto(types.toMap, dependencyTypes))
  }

  def fetchDependencies(deps: Set[String]): ValidationNel[String, Set[String]] = {
    for {
      additional <- {
        deps.map(protobufRoot / _).map(path => {
          if (path.exists) {
            for {
              proto <- parseProtoFile(path)
              childDeps <- fetchDependencies(proto.getDependencies.asScala.toSet)
            } yield (childDeps)
          } else {
            s"${path} is not found.".failure[Set[String]].toValidationNel
          }
        }).toList.sequenceU
      }
    } yield (deps ++ additional.flatten)
  }

  def getDependencyTypes(deps: Set[String]): ValidationNel[String, Map[String, List[ProtoField]]] = {
    deps.toList.map(dep => {
      val result = ProtoSchemaParser.parse((protobufRoot / dep).toFile)
      result.getTypes.asScala.map {
        case mt: MessageType => (dep, mt, result)
      }.map(fetchType).toList.sequenceU match {
        case Success(types) => types.toMap.successNel[String]
        case Failure(f) => f.failure[Map[String, List[ProtoField]]]
      }
    }).sequenceU match {
      case Success(types) => {
        types.foldLeft(Map.empty[String, List[ProtoField]])((left, right) => left ++ right).successNel[String]
      }
      case Failure(f) => f.failure[Map[String, List[ProtoField]]]
    }

  }

  def fetchType(tuple: (String, MessageType, ProtoFile)): ValidationNel[String, (String, List[ProtoField])] = {
    val nestedTypes = tuple._2.getNestedTypes.asScala.map(t => (t.getName, t)).toMap

    val token = tuple._1.split("/")
    val fqdnParts = token.slice(0, token.length -1).toList

    val otherMap = tuple._3.getTypes.asScala.map {
      case mt: MessageType => (mt.getName, mt)
    }.toMap

    def fetchProtoFields(fields: List[MessageType.Field], nestedTypes: Map[String, com.squareup.protoparser.Type], otherMap: Map[String, MessageType]): ValidationNel[String, (String, List[ProtoField])] = {
      fields.sortBy(_.getTag).toList.map { value =>
        ProtoFieldLabel.valueOf(value.getLabel) match {
          case Failure(f) => f.failure[ProtoField]
          case Success(label) => {
            (nestedTypes.get(value.getType).map {
              case et: EnumType => {
                ProtoField(
                  ProtoFieldTypes.ENUM(value.getType, et.getValues.asScala.map(e => (e.getName -> e.getTag)).toMap, label),
                  value.getName, value.getTag
                ).successNel[String]
              }
              case mt: MessageType => {
                for {
                  result <- fetchProtoFields(mt.getFields.asScala.toList, mt.getNestedTypes.asScala.map(t => (t.getName, t)).toMap, otherMap)
                } yield {
                  ProtoField(MESSAGE(mt.getName, label, result._2), value.getName, value.getTag)
                }
              }
            }).getOrElse {
              otherMap.get(value.getType) match {
                case Some(t) => {
                  ProtoField(
                    ProtoFieldTypes.valueOf(fqdnParts ++ List(t.getName) mkString("", ".", ""), label),
                    value.getName,
                    value.getTag
                  ).successNel[String]
                }
                case None => {
                  ProtoField(
                    ProtoFieldTypes.valueOf(value.getType, label),
                    value.getName,
                    value.getTag
                  ).successNel[String]
                }
              }
            }
          }
        }
      }
    }.sequenceU match {
      case Success(fields) => {
        val fqdn = fqdnParts ++ List(tuple._2.getName) mkString("", ".", "")
        (fqdn -> fields).successNel[String]
      }
      case Failure(f) => f.failure[(String, List[ProtoField])]
    }

    fetchProtoFields(tuple._2.getFields.asScala.toList, nestedTypes, otherMap)
  }

}
