name := "aeromock-server"

description := "Aeromock server daemon."

libraryDependencies ++= Seq(
  "io.netty" % "netty-all" % nettyVersion,
  "org.json4s" %% "json4s-native" % "3.2.10",
  "org.yaml" % "snakeyaml" % "1.13",
  "joda-time" % "joda-time" % "2.3",
  "org.joda" % "joda-convert" % "1.6",
  "org.javassist" % "javassist" % "3.18.2-GA",
  "org.apache.commons" % "commons-lang3" % "3.1",
  "args4j" % "args4j" % "2.0.28",
  "org.glassfish" % "javax.el" % "3.0.0",
  "com.google.protobuf" % "protobuf-java" % "2.6.0",
  "com.squareup" % "protoparser" % "3.1.4"
)
