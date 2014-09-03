package jp.co.cyberagent.aeromock.data

import org.specs2.mutable.{Tables, Specification}

import scala.collection.JavaConverters._

/**
 *
 * @author stormcat24
 */
class DynamicMethodValueStoreSpec extends Specification with Tables {


  "initialize" in {
    val map = new java.util.HashMap[String, java.util.Map[String, Any]]
    map.put("testkey", new java.util.HashMap[String, Any])
    DynamicMethodValueStore.threadLocal.set(map)

    DynamicMethodValueStore.initialize
    DynamicMethodValueStore.threadLocal.get().isEmpty must beTrue
  }

  "put" should {
    "invalid arguments" in {
      DynamicMethodValueStore.initialize
      DynamicMethodValueStore.put(null, null, "testvalue") must throwA[IllegalArgumentException]
      DynamicMethodValueStore.put("", "", "testvalue") must throwA[IllegalArgumentException]
    }

    "test" in {
      DynamicMethodValueStore.initialize

      DynamicMethodValueStore.put("jp.co.cyberagent.aeromock.Test1", "execute11", 100)
      DynamicMethodValueStore.put("jp.co.cyberagent.aeromock.Test1", "execute12", "test")
      DynamicMethodValueStore.put("jp.co.cyberagent.aeromock.Test2", "execute21", 100)
      DynamicMethodValueStore.put("jp.co.cyberagent.aeromock.Test2", "execute22", "test")

      val map = DynamicMethodValueStore.threadLocal.get()
      map.get("jp.co.cyberagent.aeromock.Test1").asScala must havePairs("execute11" -> 100, "execute12" -> "test")
      map.get("jp.co.cyberagent.aeromock.Test2").asScala must havePairs("execute21" -> 100, "execute22" -> "test")
    }
  }

  "fetch" should {
    "invalid arguments" in {
      DynamicMethodValueStore.initialize
      DynamicMethodValueStore.fetch(null, null) must throwA[IllegalArgumentException]
      DynamicMethodValueStore.fetch("", "") must throwA[IllegalArgumentException]
    }

    "test" in {
      DynamicMethodValueStore.initialize

      DynamicMethodValueStore.put("jp.co.cyberagent.aeromock.Test1", "execute11", 100)
      DynamicMethodValueStore.put("jp.co.cyberagent.aeromock.Test1", "execute12", "test")
      DynamicMethodValueStore.put("jp.co.cyberagent.aeromock.Test2", "execute21", 100)
      DynamicMethodValueStore.put("jp.co.cyberagent.aeromock.Test2", "execute22", "test")

      DynamicMethodValueStore.fetch("jp.co.cyberagent.aeromock.Test1", "execute11") must_== 100
      DynamicMethodValueStore.fetch("jp.co.cyberagent.aeromock.Test1", "execute12") must_== "test"
      DynamicMethodValueStore.fetch("jp.co.cyberagent.aeromock.Test2", "execute21") must_== 100
      DynamicMethodValueStore.fetch("jp.co.cyberagent.aeromock.Test2", "execute22") must_== "test"
    }
  }
}
