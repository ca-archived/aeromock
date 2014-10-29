name := "aeromock-spec-support"

description := "Aeromock Spec support"

libraryDependencies ++= Seq(
  "org.scaldi" %% "scaldi" % scaldiVersion,
  "io.netty" % "netty-all" % nettyVersion,
  "org.specs2" %% "specs2" % "2.3.12"
)
