name := "deduplication"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  "edu.stanford.nlp" % "stanford-corenlp" % "3.9.2",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.9.2" classifier "models",
  "com.typesafe.akka" %% "akka-actor" % "2.5.21",
  "com.typesafe.akka" %% "akka-stream" % "2.5.21",
  "org.scala-lang.modules" %% "scala-xml" % "1.2.0",
  "org.apache.kafka" %% "kafka" % "2.1.0",
  "org.apache.spark" %% "spark-core" % "2.3.0",
  "org.apache.spark" %% "spark-sql" % "2.3.0",
  "org.apache.spark" %% "spark-streaming" % "2.3.0",
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.3.0" excludeAll(
    ExclusionRule(organization = "org.spark-project.spark", name = "unused"),
    ExclusionRule(organization = "org.apache.spark", name = "spark-streaming"),
    ExclusionRule(organization = "org.apache.hadoop")
  ),
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.5.0",
  "com.google.code.gson" % "gson" % "2.8.5",
  "org.mongodb.spark" %% "mongo-spark-connector" % "2.3.0",
  "io.spray" %% "spray-json" % "1.3.5"
)

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

initialize := {
  val _ = initialize.value
  if (sys.props("java.specification.version") != "1.8")
    sys.error("Java 8 is required for this project.")
}

dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.7"

mainClass in Compile := some("OneTime")
assemblyJarName := "deduplication-fat.jar"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _@_*) => MergeStrategy.discard
  case PathList("reference.conf") => MergeStrategy.concat
  case _ => MergeStrategy.first
}
