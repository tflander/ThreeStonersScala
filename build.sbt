name := """scala-testing"""

version := "0.1.0"

// scalaVersion := "2.11.2"
scalaVersion := "2.10.4"

// ScalaTest
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.2" % "test"


libraryDependencies ++= Seq(
  "org.mockito" % "mockito-core" % "1.10.8" % "test"
)

