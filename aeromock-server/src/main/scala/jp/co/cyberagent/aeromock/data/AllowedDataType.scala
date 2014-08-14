package jp.co.cyberagent.aeromock.data


sealed abstract class AllowedDataType(val extensions: List[String])

object AllowedDataType  {

  case object JSON extends AllowedDataType(List("json"))
  case object YAML extends AllowedDataType(List("yaml", "yml"))

}
