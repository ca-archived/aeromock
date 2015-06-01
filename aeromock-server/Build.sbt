name := "aeromock-server"

description := "Aeromock server daemon."

libraryDependencies ++= Seq(
  "io.netty" % "netty-all" % nettyVersion,
  "org.json4s" %% "json4s-native" % "3.2.11",
  "org.yaml" % "snakeyaml" % "1.15",
  "joda-time" % "joda-time" % "2.3",
  "org.joda" % "joda-convert" % "1.6",
  "org.javassist" % "javassist" % "3.18.2-GA",
  "org.apache.commons" % "commons-lang3" % "3.1",
  "args4j" % "args4j" % "2.0.29",
  "org.glassfish" % "javax.el" % "3.0.0",
  "com.google.protobuf" % "protobuf-java" % "2.6.1",
  "com.squareup" % "protoparser" % "3.1.5",
  "org.msgpack" %% "msgpack-scala" % "0.6.11"
)
