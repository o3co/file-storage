import sbt._
import Keys._

object Build extends Build 
{
  import BuildSettings._
  import Dependencies._

  lazy val root = Project("root", file("."))
    .settings(basicSettings: _*)
    .aggregate(
      // 
      FileStoreProjects.root
    )
    .settings(
      aggregate in update := false
    )

  lazy val common = Project("common", file("common"))
    .settings(basicSettings: _*)
    .settings(
      libraryDependencies ++= 
        compile (
          scalaLogging
        ) ++ 
        provided (
          akkaActor,
          scalaReflect,
          sprayRouting,
          typesafeConfig
        )
    )
}
