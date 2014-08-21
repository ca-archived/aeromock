package jp.co.cyberagent.aeromock.core.el

import org.specs2.mutable.{Specification, Tables}

/**
 *
 * @author stormcat24
 */
class ELContextSpec extends Specification with Tables {

  "eval" should {

    "not expression" in {
      val el = new ELContext
      el.eval(null) must_== null
      el.eval("") must_== ""
      el.eval(" ") must_== " "
      el.eval("test") must_== "test"
      el.eval("$test") must_== "$test"
      el.eval("${test") must_== "${test"
      el.eval("1 + 2") must_== "1 + 2"
    }

    "single expression" in {
      val el = new ELContext

      "expression"                                         | "expect"       |
      "${1}"                                               ! 1              |
      "${true}"                                            ! true           |
      """${"test"}"""                                      ! "test"         |
      "${1+2}"                                             ! 3              |
      "${2 + 3}"                                           ! 5              |
      "${(2 + 3) * 5}"                                     ! 25             |
      """${"test".length()}"""                             ! 4              |
      """${"prefix" += "suffix"}"""                        ! "prefixsuffix" |
      """${"prefix".concat("suffix")}"""                   ! "prefixsuffix" |
      """${"prefix".concat("suffix")}"""                   ! "prefixsuffix" |
      """${map={"key1":1,"key2":2}; map.get("key1")}"""    ! 1              |
      """${map={"key1":1,"key2":2}; map.key1}"""           ! 1              |
      """${map={"key1":1,"key2":2}; map["key1"]}"""        ! 1              |
      """${map={"key1":1,"key2":2}; map.key99}"""          ! null           |
      """${list=[100, 99, 98]; list.get(0)}"""             ! 100            |
      """${list=[100, 99, 98]; list[0]}"""                 ! 100            |> {
        (expression, expect) => {
          el.eval(expression) must_== expect
        }
      }
    }

    "single expression with context map" in {
      val el = new ELContext(Map(
        "globalString" -> "global1Value",
        "globalInt" -> 1000,
        "globalMap" -> Map(
          "id" -> 5000,
          "name" -> "testname"
        ),
        "globalList" -> List(100, 200, 300)
      ))

      "expression"                                         | "expect"       |
      "${globalString}"                                    ! "global1Value" |
      "${globalInt}"                                       ! 1000           |
      "${globalMap.id}"                                    ! 5000           |
      "${globalMap.name}"                                  ! "testname"     |
      "${globalList[0]}"                                   ! 100            |> {
        (expression, expect) => {
          el.eval(expression) must_== expect
        }
      }
    }

    "multi expression" in {
      val el = new ELContext()

      "expression"                                       | "expect"       |
      "${1 + 2}_${2 + 3}"                                ! "3_5"          |
      "_${1 + 2}_${2 + 3}_${3 + 4}_"                     ! "_3_5_7_"      |
      """${"test".length()}"""                           ! 4              |> {
        (expression, expect) => {
          el.eval(expression) must_== expect
        }
      }

    }

  }

}
