import sbt._
import Keys._

import spray.revolver.RevolverPlugin._
import sbtassembly.AssemblyKeys._
import sbtassembly.MergeStrategy

object BuildSettings {
  val VERSION      = "0.1"
  val ScalaVersion = "2.11.7"

  import Dependencies._

  val basicSettings = Seq(
      organization  := "1o1 co, Ltd",
      scalaVersion  := ScalaVersion,
      version       := VERSION,
      scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8"),
      resolvers     ++= Dependencies.resolutionRepos,
      parallelExecution in Test := false,
      javaOptions in Test += "-Dorg.slf4j.simpleLogger.defaultLogLevel=OFF",
      updateOptions in Global := updateOptions.in(Global).value.withCachedResolution(true),
      libraryDependencies ++= 
        test(
          specs2
        ),
      assemblyMergeStrategy in assembly := {
        case "application.conf" => MergeStrategy.discard
        case x => 
          val oldStrategy = (assemblyMergeStrategy in assembly).value
          oldStrategy(x)
      }
    )

  lazy val bootableSettings = Seq(
      libraryDependencies ++= 
        provided (
          akkaActor, 
          scalaLogging,
          slf4jApi,
          scopt,
          typesafeConfig
        ) ++
        runtime (
          akkaActor, 
          scalaLogging,
          slf4jApi,
          scopt,
          typesafeConfig,
          logback
        )
    )

  lazy val restSettings = 
    Seq(
      libraryDependencies ++= 
        provided (
          sprayCan,
          sprayRouting
        ) ++
        runtime (
          sprayCan,
          sprayRouting
        )
    ) ++ 
    Revolver.settings
}
