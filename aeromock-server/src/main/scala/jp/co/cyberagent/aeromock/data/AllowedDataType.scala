package jp.co.cyberagent.aeromock.data


sealed abstract class AllowedDataType(val extensions: Seq[String])

object AllowedDataType  {

  case object JSON extends AllowedDataType(Seq("json"))
  case object YAML extends AllowedDataType(Seq("yaml", "yml"))

  val extensions = Seq(JSON, YAML).flatMap(_.extensions)
}
