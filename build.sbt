name := """project"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.8"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "5.0.2",
  "com.typesafe.play" %% "play-slick-evolutions" % "5.0.2",
  "mysql" % "mysql-connector-java" % "8.0.29"
)

libraryDependencies += "com.github.krzemin" %% "octopus" % "0.4.1"

libraryDependencies += "org.mindrot" % "jbcrypt" % "0.4"

libraryDependencies += "com.github.jwt-scala" %% "jwt-play-json" % "9.0.5"
libraryDependencies += "com.github.jwt-scala" %% "jwt-core" % "9.0.5"