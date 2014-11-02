package jp.co.cyberagent.aeromock.protobuf

import java.nio.file.Path

import com.google.protobuf.{CodedOutputStream, ByteString}
import jp.co.cyberagent.aeromock.AeromockTestModule
import jp.co.cyberagent.aeromock.config.definition.ProjectDef
import jp.co.cyberagent.aeromock.test.{RequestScope, SpecSupport}
import org.specs2.mutable.{Specification, Tables}
import protobuf.api.Enum.WithEnumResponse.TestEnum
import protobuf.api.VariousTag.VariousTagResponse.VariousEnum
import protobuf.api._
import protobuf.schema.JobOuterClass.Job
import protobuf.schema.UserOuterClass.{UserStatus, User}
import protobuf.schema.VariousNestOuterClass.VariousNest

/**
 *
 * @author stormcat24
 */
class ProtobufResponseServiceSpec extends Specification with Tables with SpecSupport {

  "/api/various_tag.proto" should {
    implicit val module = new AeromockTestModule {
      override val projectConfigPath: Path = getResourcePath(".").resolve("../../../../tutorial/protobuf/project.yaml").toRealPath()
      override val projectDefArround = (projectDef: ProjectDef) => {}
    }

    "various_tag.yaml" in RequestScope {
      val expected = VariousTag.VariousTagResponse.newBuilder
        // tag size = 1
        .setInt32TagSize1(100)
        .setInt32OptTagSize1(100)
        .addInt32ListTagSize1(101)
        .addInt32ListTagSize1(201)
        .setEnumTagSize1(VariousEnum.ENUM1)
        .setEnumOptTagSize1(VariousEnum.ENUM1)
        .addEnumListTagSize1(VariousEnum.ENUM1)
        .addEnumListTagSize1(VariousEnum.ENUM2)
        .addEnumListTagSize1(VariousEnum.ENUM3)
        .setNestTagSize1(VariousNest.newBuilder.setTextTagSize1("text11").setTextTagSize2("text12").setTextTagSize3("text13").build)
        .setNestOptTagSize1(VariousNest.newBuilder.setTextTagSize1("text11").setTextTagSize2("text12").setTextTagSize3("text13").build)
        .addNestListTagSize1(VariousNest.newBuilder.setTextTagSize1("text11").setTextTagSize2("text12").setTextTagSize3("text13").build)
        // tag size = 2
        .setInt32TagSize2(200)
        .setInt32OptTagSize2(200)
        .addInt32ListTagSize2(102)
        .addInt32ListTagSize2(202)
        .setEnumTagSize2(VariousEnum.ENUM2)
        .setEnumOptTagSize2(VariousEnum.ENUM2)
        .addEnumListTagSize2(VariousEnum.ENUM1)
        .addEnumListTagSize2(VariousEnum.ENUM2)
        .addEnumListTagSize2(VariousEnum.ENUM3)
        .setNestTagSize2(VariousNest.newBuilder.setTextTagSize1("text21").setTextTagSize2("text22").setTextTagSize3("text23").build)
        .setNestOptTagSize2(VariousNest.newBuilder.setTextTagSize1("text21").setTextTagSize2("text22").setTextTagSize3("text23").build)
        .addNestListTagSize2(VariousNest.newBuilder.setTextTagSize1("text21").setTextTagSize2("text22").setTextTagSize3("text23").build)
        // tag size = 3
        .setInt32TagSize3(300)
        .setInt32OptTagSize3(300)
        .addInt32ListTagSize3(103)
        .addInt32ListTagSize3(203)
        .setEnumTagSize3(VariousEnum.ENUM3)
        .setEnumOptTagSize3(VariousEnum.ENUM3)
        .addEnumListTagSize3(VariousEnum.ENUM1)
        .addEnumListTagSize3(VariousEnum.ENUM2)
        .addEnumListTagSize3(VariousEnum.ENUM3)
        .setNestTagSize3(VariousNest.newBuilder.setTextTagSize1("text31").setTextTagSize2("text32").setTextTagSize3("text33").build)
        .setNestOptTagSize3(VariousNest.newBuilder.setTextTagSize1("text31").setTextTagSize2("text32").setTextTagSize3("text33").build)
        .addNestListTagSize3(VariousNest.newBuilder.setTextTagSize1("text31").setTextTagSize2("text32").setTextTagSize3("text33").build)
        .build

      val result = ProtobufResponseService.render(request("/various_tag"))
      result.content must_== expected.toByteArray
    }
  }

  "/api/simple.proto" should {
    implicit val module = new AeromockTestModule {
      override val projectConfigPath: Path = getResourcePath(".").resolve("../../../../tutorial/protobuf/project.yaml").toRealPath()
      override val projectDefArround = (projectDef: ProjectDef) => {}
    }

    "simple.yaml" in RequestScope {
      val expected = Simple.SimpleResponse.newBuilder
        .setInt32Value(10011)
        .setInt32OptValue(10111)
        .setInt64Value(11011L)
        .setInt64OptValue(11111L)
        .setUint32Value(12011)
        .setUint32OptValue(12111)
        .setUint64Value(13011L)
        .setUint64OptValue(13111L)
        .setSint32Value(14011)
        .setSint32OptValue(14111)
        .setSint64Value(15011L)
        .setSint64OptValue(15111L)
        .setFixed32Value(16011)
        .setFixed32OptValue(16111)
        .setFixed64Value(17011L)
        .setFixed64OptValue(17111L)
        .setSfixed32Value(18011)
        .setSfixed32OptValue(18111)
        .setSfixed64Value(19011L)
        .setSfixed64OptValue(19111L)
        .setFloatValue(200.11f)
        .setFloatOptValue(201.11f)
        .setDoubleValue(210.11)
        .setDoubleOptValue(211.11)
        .setBoolValue(true)
        .setBoolOptValue(true)
        .setStringValue("stringValue")
        .setStringOptValue("stringOptValue")
        .setBytesValue(ByteString.copyFromUtf8("bytesValue"))
        .setBytesOptValue(ByteString.copyFromUtf8("bytesOptValue"))
        .addStringList("string1")
        .addStringList("string2")
        .addInt32List(100)
        .addInt32List(200)
        .addInt32List(-100)
        .addInt64List(8000000000L)
        .addInt64List(9000000000L)
        .addInt64List(-8000000000L)
        .addUint32List(101)
        .addUint32List(201)
        .addUint64List(8000000001L)
        .addUint64List(9000000001L)
        .addSint32List(102)
        .addSint32List(202)
        .addSint32List(-102)
        .addSint64List(8000000002L)
        .addSint64List(9000000002L)
        .addSint64List(-8000000002L)
        .addFixed32List(103)
        .addFixed32List(203)
        .addFixed32List(-103)
        .addFixed64List(8000000003L)
        .addFixed64List(9000000003L)
        .addFixed64List(-8000000003L)
        .addSfixed32List(104)
        .addSfixed32List(204)
        .addSfixed32List(-104)
        .addSfixed64List(8000000004L)
        .addSfixed64List(9000000004L)
        .addSfixed64List(-8000000004L)
        .addFloatList(111.1f)
        .addFloatList(222.2f)
        .addFloatList(-333.3f)
        .addDoubleList(8000000000.1)
        .addDoubleList(9000000000.1)
        .addDoubleList(-8000000000.1)
        .addBoolList(true)
        .addBoolList(false)
        .build

      val result = ProtobufResponseService.render(request("/simple"))
      result.content must_== expected.toByteArray
    }

    "simple__optional.yaml" in RequestScope {
      val expected = Simple.SimpleResponse.newBuilder
        .setInt32Value(10011)
        .setInt64Value(11011L)
        .setUint32Value(12011)
        .setUint64Value(13011L)
        .setSint32Value(14011)
        .setSint64Value(15011L)
        .setFixed32Value(16011)
        .setFixed64Value(17011L)
        .setSfixed32Value(18011)
        .setSfixed64Value(19011L)
        .setFloatValue(200.11f)
        .setDoubleValue(210.11)
        .setBoolValue(true)
        .setStringValue("stringValue")
        .setBytesValue(ByteString.copyFromUtf8("bytesValue"))
        .build
      val result = ProtobufResponseService.render(request("/simple?_dataid=optional"))

      result.content must_== expected.toByteArray
    }
  }


  "/api/enum.proto" should {
    "test" in RequestScope {
      implicit val module = new AeromockTestModule {
        override val projectConfigPath: Path = getResourcePath(".").resolve("../../../../tutorial/protobuf/project.yaml").toRealPath()
        override val projectDefArround = (projectDef: ProjectDef) => {}
      }

      val expected = Enum.WithEnumResponse.newBuilder
        .setProp1(100)
        .setProp2(TestEnum.KEY2)
        .addEnumList(TestEnum.KEY1)
        .addEnumList(TestEnum.KEY3)
        .build

      val result = ProtobufResponseService.render(request("/enum"))
      result.content must_== expected.toByteArray
    }
  }

  "/api/nest1.proto" should {
    implicit val module = new AeromockTestModule {
      override val projectConfigPath: Path = getResourcePath(".").resolve("../../../../tutorial/protobuf/project.yaml").toRealPath()
      override val projectDefArround = (projectDef: ProjectDef) => {}
    }

    "test" in RequestScope {
      val expected = Nest1.Nest1Response.newBuilder
        .setId(100)
        .setMainUser(
          User.newBuilder
            .setId(1).setName("メインユーザー").setStatus(
              UserStatus.newBuilder.setAge(50).setJob(
                Job.newBuilder.setId(100).setName("programmer").build
              ).build
            ).build
        )
        .addOtherUsers(
          User.newBuilder
            .setId(11).setName("他のユーザー11").setStatus(
              UserStatus.newBuilder.setAge(21).setJob(
                Job.newBuilder.setId(101).setName("designer").build
              ).build
            ).build
        )
        .addOtherUsers(
          User.newBuilder
            .setId(12).setName("他のユーザー12").setStatus(
              UserStatus.newBuilder.setAge(25).setJob(
                Job.newBuilder.setId(102).setName("illustrator").build
              ).build
            ).build
        )
        .build

      expected.toByteArray
      val result = ProtobufResponseService.render(request("/nest1"))
      result.content must_== expected.toByteArray
    }
  }

}
