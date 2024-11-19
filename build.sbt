ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.15"

lazy val root = (project in file("."))
  .settings(
    name := "FP_Project"
  )

libraryDependencies ++= Seq(
  "org.mongodb.scala" %% "mongo-scala-driver" % "4.8.0",
  "org.neo4j.driver" % "neo4j-java-driver" % "5.11.0",
  "org.apache.spark" %% "spark-core" % "3.5.0",
  "org.apache.spark" %% "spark-sql" % "3.5.0",
  "org.mongodb.spark" %% "mongo-spark-connector" % "10.1.0",
)

Compile / run / fork := true

javaOptions ++= Seq(
  "--add-exports", "java.base/sun.nio.ch=ALL-UNNAMED",
  "--add-opens", "java.base/java.nio=ALL-UNNAMED",
  "--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED",
  "--add-opens", "java.base/java.util=ALL-UNNAMED",
  "--add-opens", "java.base/java.lang=ALL-UNNAMED"
)
