import sbt._
import Keys._

object FileStoreProjects extends Build 
{
  import BuildSettings._
  import Dependencies._
  import Build._

  lazy val root = Project("file-store", file("file-store"))
    .settings(basicSettings: _*)
    .aggregate(
      core, 
      metaRest,
      metaServer, 
      rest,
      util,
      standalone
    )
    .settings(
      aggregate in update := false
    )

  lazy val core = Project("file-store-core", file("file-store/core"))
    .settings(basicSettings: _*)
    .settings(
      libraryDependencies ++= 
        compile(
          commonsIO,
          sprayHttp,
          sprayHttpx
        ) ++
        provided (
          akkaActor,
          sprayRouting,
          json4sNative
        )
    )
    .dependsOn(Build.common)

  lazy val metaRest = Project("file-store-meta-rest", file("file-store/meta-rest"))
    .settings(basicSettings: _*)
    .settings(bootableSettings: _*)
    .settings(restSettings: _*)
    .settings(
      libraryDependencies ++= 
        compile(
          json4sNative,
          o3coUtilRest
        ) ++ provided (
          scopt
        ) ++ runtime (
          h2,
          mysqlConnector,
          scopt
        )
    )
    .dependsOn(
      core,
      metaServer % "runtime"
    )

  lazy val metaServer = Project("file-store-meta-server", file("file-store/meta-server"))
    .settings(basicSettings: _*)
    .settings(bootableSettings: _*)
    .settings(
      libraryDependencies ++= 
        compile (
          c3p0,
          slick,
          slickJodaMapper,
          jodaTime,
          joda,
          o3coUtilGenerator
        ) ++ 
        provided (
          scopt
        ) ++
        runtime (
          h2,
          mysqlConnector,
          scopt
        )
    )
    .dependsOn(
      core,
      util
    )

  lazy val rest = Project("file-store-rest", file("file-store/rest"))
    .settings(basicSettings: _*)
    .settings(bootableSettings: _*)
    .settings(restSettings: _*)
    .settings(
      libraryDependencies ++= 
        compile(
          json4sNative,
          o3coUtilRest
        ) ++ 
        provided (
          scopt
        ) ++
        runtime (
          scopt,
          h2,
          mysqlConnector
        )
    )
    .dependsOn(
      core,
      util,
      metaServer % "runtime",
      server % "provided,runtime"
    )

  lazy val server = Project("file-store-server", file("file-store/server"))
    .settings(basicSettings: _*)
    .settings(bootableSettings: _*)
    .settings(
      libraryDependencies ++= 
        provided (
          scalaLogging,
          scopt
        ) ++ 
        runtime (
          scalaLogging,
          scopt
        )
    )
    .dependsOn(
      core, 
      util
    )

  lazy val util = Project("file-store-util", file("file-store/util"))
    .settings(basicSettings: _*)
    .settings(
      libraryDependencies ++= 
        provided (
          akkaActor,
          commonsIO
        )
    )
    .dependsOn(
      core
    )

  lazy val standalone = Project("file-store-standalone", file("file-store/standalone"))
    .settings(basicSettings: _*)
    .settings(bootableSettings: _*)
    .settings(restSettings: _*)
    .settings(
      libraryDependencies ++= 
        runtime (
          h2,
          mysqlConnector
        )
    )
    .dependsOn(
      core, 
      server,
      rest,
      metaServer,
      metaRest
    )
}

