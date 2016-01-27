import sbt._


object Dependencies {
  import BuildSettings._

  val resolutionRepos = Seq(
    "spray repo" at "http://repo.spray.io/",
    "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  )

  // 
  def compile   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile")
  def provided  (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "provided")
  def test      (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")
  def runtime   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "runtime")
  def container (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "container")

  // Dependencies
  //val scalaLogging    = "com.typesafe.scala-logging" % "scala-logging-slf4j_2.10" % "2.1.2"
  val akkaActor       = "com.typesafe.akka"   %% "akka-actor"      % "2.3.14"
  val akkaCluster     = "com.typesafe.akka"   %% "akka-cluster"    % "2.3.14"
  val akkaClusterTools = "com.typesafe.akka"   %% "akka-cluster-tools" % "2.3.14"
  val akkaContrib     = "com.typesafe.akka"   %% "akka-contrib"    % "2.3.14"
  val akkaPersistence = "com.typesafe.akka"   %% "akka-persistence-experimental" % "2.3.14"
  val akkaRemote      = "com.typesafe.akka"   %% "akka-remote"     % "2.3.14"
  val akkaSlf4j       = "com.typesafe.akka"   %% "akka-slf4j"      % "2.3.14"
  val akkaTestKit     = "com.typesafe.akka"   %% "akka-testkit"    % "2.3.14"
  val c3p0            = "com.mchange"         %  "c3p0"            % "0.9.5"
  val commonsIO       = "commons-io"          % "commons-io"       % "2.4"
  val joda            = "org.joda"            % "joda-convert"     % "1.7"
  val jodaTime        = "joda-time"           % "joda-time"        % "2.7"
  val json4sCore      = "org.json4s"          %% "json4s-core"     % "3.2.11"
  val json4sExt       = "org.json4s"          %% "json4s-ext"      % "3.2.11"
  val json4sNative    = "org.json4s"          %% "json4s-native"   % "3.2.11"
  val h2              = "com.h2database"      %  "h2"              % "1.4.187"
  val logback         = "ch.qos.logback"      %  "logback-classic" % "1.1.3"
  val mysqlConnector  = "mysql"               %  "mysql-connector-java"  % "5.1.35"
  //val scalaLogging    = "com.typesafe.scala-logging" % "scala-logging-slf4j_2.11" % "2.1.2"
  val scalaLogging    = "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"
  val scalaReflect    = "org.scala-lang"      %  "scala-reflect"   % BuildSettings.ScalaVersion
  val scalaTest       = "org.scalatest"       %% "scalatest"       % "2.2.4"
  val scopt           = "com.github.scopt"    %% "scopt"           % "3.3.0"
  val slf4jApi        = "org.slf4j"           %  "slf4j-api"       % "1.7.12"
  val slf4jNop        = "org.slf4j"           %  "slf4j-nop"       % "1.7.12"
  val slick           = "com.typesafe.slick"  %% "slick"           % "3.1.0"
  val slickJodaMapper = "com.github.tototoshi" %% "slick-joda-mapper" % "2.1.0"
  val specs2          = "org.specs2"          %% "specs2-core"     % "2.4.17"
  val sprayCaching    = "io.spray"            %% "spray-caching"   % "1.3.3"
  val sprayCan        = "io.spray"            %% "spray-can"       % "1.3.3"
  val sprayJson       = "io.spray"            %% "spray-json"      % "1.3.2"
  val sprayRouting    = "io.spray"            %% "spray-routing"   % "1.3.3"
  val sprayHttp       = "io.spray"            %% "spray-http"      % "1.3.3"
  val sprayHttpx      = "io.spray"            %% "spray-httpx"      % "1.3.3"
  val sprayTestKit    = "io.spray"            %% "spray-testkit"   % "1.3.3"
  val typesafeConfig  = "com.typesafe"        %  "config"          % "1.2.1"

  val o3coUtilCounter  = "jp.o3co"             %% "util-counter"    % "0.1-SNAPSHOT"
  val o3coUtilGenerator= "jp.o3co"             %% "util-generator"  % "0.1-SNAPSHOT"
  val o3coUtilRest     = "jp.o3co"             %% "util-rest"       % "0.1-SNAPSHOT"
}
