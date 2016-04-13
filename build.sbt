import sbt.Keys._
import com.twitter.scrooge.ScroogeSBT.autoImport._

lazy val projectSettings = Defaults.coreDefaultSettings ++ Seq(
  organization := "ru.livetex",
  scalaVersion := "2.11.7",
  isSnapshot := true
)

lazy val thriftDirectory = settingKey[File]("The folder containing the thrift IDL files.")

lazy val thriftIDLFiles = settingKey[Seq[File]]("The thrift IDL files.")

lazy val api = Project(
  id = "api",
  base = file("api"),
  settings = projectSettings ++ Seq(
    name := "api",
    version := "0.0.1"
  ))
  .disablePlugins(ScroogeSBT)
  .settings(
    thriftDirectory := baseDirectory.value / "src" / "main" / "thrift",
    thriftIDLFiles := (thriftDirectory.value ** "*.thrift").get,
    mappings in (Compile, packageBin) :=
      thriftIDLFiles.value map { thriftFile => (thriftFile, thriftFile.name) }
  )

lazy val service = Project(
  id = "service",
  base = file("service"),
  settings = projectSettings ++ Seq(
    name := "service",
    version := "0.0.1",
    libraryDependencies ++= Seq(
      "ru.livetex" %% "api" % "0.0.1",
      "org.apache.thrift" % "libthrift" % "0.8.0",
      "com.twitter" %% "scrooge-core" % "4.6.0",
      "com.twitter" %% "finagle-thrift" % "6.34.0"
    )
  ))
  .settings(
    scroogeThriftDependencies in Compile := Seq("api_2.11"),
    scroogeThriftSources in Compile ++=
      (scroogeUnpackDeps in Compile).value.flatMap { dir => (dir ** "*.thrift").get }
  )
