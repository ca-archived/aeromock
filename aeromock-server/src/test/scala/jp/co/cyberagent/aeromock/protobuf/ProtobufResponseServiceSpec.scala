package jp.co.cyberagent.aeromock.protobuf

import java.nio.file.Path

import com.google.protobuf.ByteString
import jp.co.cyberagent.aeromock.AeromockTestModule
import jp.co.cyberagent.aeromock.config.definition.ProjectDef
import jp.co.cyberagent.aeromock.test.{RequestScope, SpecSupport}
import org.specs2.mutable.{Specification, Tables}
import protobuf.api.Enum.WithEnumResponse.TestEnum
import protobuf.api._
import protobuf.schema.JobOuterClass.Job
import protobuf.schema.UserOuterClass.{UserStatus, User}

/**
 *
 * @author stormcat24
 */
class ProtobufResponseServiceSpec extends Specification with Tables with SpecSupport {

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

      val result = ProtobufResponseService.render(request("/nest1"))
      result.content must_== expected.toByteArray
    }
  }

}
