package jp.co.cyberagent.aeromock.msgpack

import java.nio.file.Path

import jp.co.cyberagent.aeromock.AeromockTestModule
import jp.co.cyberagent.aeromock.config.definition.ProjectDef
import jp.co.cyberagent.aeromock.test.{RequestScope, SpecSupport}
import jp.co.cyberagent.aeromock.helper._
import org.msgpack.ScalaMessagePack
import org.specs2.mutable.{Tables, Specification}
import MessagepackResponseService._
import org.msgpack.`type`.ValueFactory._

/**
 *
 * @author stormcat24
 */
class MessagepackResponseServiceSpec extends Specification with Tables with SpecSupport {

  "compact array type" should {
    implicit val module = new AeromockTestModule {
      override val projectConfigPath: Path = getResourcePath(".").resolve("../../../../tutorial/messagepack/project_compact.yaml").toRealPath()
      override val projectDefArround = (projectDef: ProjectDef) => {}
    }

    "array.yaml" in RequestScope {
      val expected = createArrayValue(Array(
        createIntegerValue(100),
        createIntegerValue(200),
        createIntegerValue(300)
      ))

      val result = render(request("/array").toAeromockRequest(Map.empty))
      result.content must_== ScalaMessagePack.writeV(expected)
    }

    "array_mix.yaml" in RequestScope {
      val expected = createArrayValue(Array(
        createIntegerValue(100),
        createNilValue,
        createFloatValue(200.2),
        createBooleanValue(true),
        createBooleanValue(false),
        createIntegerValue(-300),
        createRawValue("arrayvalue1"),
        createArrayValue(Array(
          createRawValue("prop1value"),
          createRawValue("prop2value")
        )),
        createIntegerValue(999999999999L)
      ))

      val result = render(request("/array_mix").toAeromockRequest(Map.empty))
      result.content must_== ScalaMessagePack.writeV(expected)
    }
  }


  "json array type" should {
    implicit val module = new AeromockTestModule {
      override val projectConfigPath: Path = getResourcePath(".").resolve("../../../../tutorial/messagepack/project.yaml").toRealPath()
      override val projectDefArround = (projectDef: ProjectDef) => {}
    }

    "array.yaml" in RequestScope {
      val expected = createArrayValue(Array(
        createIntegerValue(100),
        createIntegerValue(200),
        createIntegerValue(300)
      ))

      val result = render(request("/array"))
      result.content must_== ScalaMessagePack.writeV(expected)
    }

    "array_mix.yaml" in RequestScope {
      val expected = createArrayValue(Array(
        createIntegerValue(100),
        createNilValue,
        createFloatValue(200.2),
        createBooleanValue(true),
        createBooleanValue(false),
        createIntegerValue(-300),
        createRawValue("arrayvalue1"),
        createMapValue(Array(
          createRawValue("prop1"),
          createRawValue("prop1value"),
          createRawValue("prop2"),
          createRawValue("prop2value")
        )),
        createIntegerValue(999999999999L)
      ))

      val result = render(request("/array_mix"))
      result.content must_== ScalaMessagePack.writeV(expected)
    }
  }

  "compact map type" should {
    implicit val module = new AeromockTestModule {
      override val projectConfigPath: Path = getResourcePath(".").resolve("../../../../tutorial/messagepack/project_compact.yaml").toRealPath()
      override val projectDefArround = (projectDef: ProjectDef) => {}
    }

    "nest1.yaml" in RequestScope {
      val expected = createArrayValue(Array(
        createIntegerValue(100),
        createArrayValue(Array(
          createIntegerValue(1),
          createRawValue("メインユーザー"),
          createArrayValue(Array(
            createIntegerValue(50),
            createArrayValue(Array(
              createIntegerValue(100),
              createRawValue("programmer")
            ))
          )),
          createNilValue
        )),
        createArrayValue(Array(
          createArrayValue(Array(
            createIntegerValue(11),
            createRawValue("他のユーザー11"),
            createArrayValue(Array(
              createIntegerValue(21),
              createArrayValue(Array(
                createIntegerValue(101),
                createRawValue("designer")
              ))
            ))
          )),
          createArrayValue(Array(
            createIntegerValue(12),
            createRawValue("他のユーザー12"),
            createArrayValue(Array(
              createIntegerValue(25),
              createArrayValue(Array(
                createIntegerValue(102),
                createRawValue("illustrator")
              ))
            ))
          ))
        ))
      ))
      val result = render(request("/nest").toAeromockRequest(Map.empty))
      result.content must_== ScalaMessagePack.writeV(expected)
    }
  }

  "json map type" should {
    implicit val module = new AeromockTestModule {
      override val projectConfigPath: Path = getResourcePath(".").resolve("../../../../tutorial/messagepack/project.yaml").toRealPath()
      override val projectDefArround = (projectDef: ProjectDef) => {}
    }

    "nest1.yaml" in RequestScope {
      val expected = createMapValue(Array(
        createRawValue("id"), createIntegerValue(100),
        createRawValue("mainUser"), createMapValue(Array(
          createRawValue("id"), createIntegerValue(1),
          createRawValue("name"), createRawValue("メインユーザー"),
          createRawValue("status"), createMapValue(Array(
            createRawValue("age"), createIntegerValue(50),
            createRawValue("job"), createMapValue(Array(
              createRawValue("id"), createIntegerValue(100),
              createRawValue("name"), createRawValue("programmer")
            ))
          )),
          createRawValue("description"), createNilValue
        )),
        createRawValue("otherUsers"), createArrayValue(Array(
          createMapValue(Array(
            createRawValue("id"), createIntegerValue(11),
            createRawValue("name"), createRawValue("他のユーザー11"),
            createRawValue("status"), createMapValue(Array(
              createRawValue("age"), createIntegerValue(21),
              createRawValue("job"), createMapValue(Array(
                createRawValue("id"), createIntegerValue(101),
                createRawValue("name"), createRawValue("designer")
              ))
            ))
          )),
          createMapValue(Array(
            createRawValue("id"), createIntegerValue(12),
            createRawValue("name"), createRawValue("他のユーザー12"),
            createRawValue("status"), createMapValue(Array(
              createRawValue("age"), createIntegerValue(25),
              createRawValue("job"), createMapValue(Array(
                createRawValue("id"), createIntegerValue(102),
                createRawValue("name"), createRawValue("illustrator")
              ))
            ))
          ))
        ))
      ))
      val result = render(request("/nest"))
      result.content must_== ScalaMessagePack.writeV(expected)
    }
  }
}
