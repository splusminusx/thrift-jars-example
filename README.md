# Thrift-Only-Jar Example

Example of Thrift-only-jar dependencies.

### Define your api project in `build.sbt`

```scala
lazy val thriftDirectory = settingKey[File]("The folder containing the thrift IDL files.")

lazy val thriftIDLFiles = settingKey[Seq[File]]("The thrift IDL files.")

lazy val api = Project(
  id = "api",
  base = file("api"),
  settings = projectSettings ++ Seq(
    name := "api",
    version := "0.0.1"
  ))
  // disable ScroogeSBT in case of multi-project usage.
  //.disablePlugins(ScroogeSBT)
  .settings(
    thriftDirectory := {
      baseDirectory.value / "src" / "main" / "thrift"
    },
    thriftIDLFiles := {
      (thriftDirectory.value ** "*.thrift").get
    },
    // pack only thrift files in resulting artifact.
    mappings in (Compile, packageBin) := {
      thriftIDLFiles.value map { thriftFile => (thriftFile, thriftFile.name) }
    }
  )
```

### Publish thrift files.

```bash
$ sbt api/publish
```

### Check files in resulting jar.
```bash
$ jar tf ~/.ivy2/local/ru.livetex/api_2.11/0.0.1/jars/api_2.11.jar

META-INF/MANIFEST.MF
deps.thrift
```

### Define your new project in `build.sbt`. This project can depend on thrift-only-jar.
```scala
lazy val service = Project(
  id = "service",
  base = file("service"),
  settings = projectSettings ++ Seq(
    name := "service",
    version := "0.0.1",
    libraryDependencies ++= Seq(
      // define dependency on thrift-only-jar.
      "ru.livetex" %% "api" % "0.0.1",
      "org.apache.thrift" % "libthrift" % "0.8.0",
      "com.twitter" %% "scrooge-core" % "4.6.0",
      "com.twitter" %% "finagle-thrift" % "6.34.0"
    )
  ))
  .settings(
    // extract thrift files from jar.
    scroogeThriftDependencies in Compile := Seq("api_2.11"),
    // compile thrift files from jar/
    scroogeThriftSources in Compile ++= {
      (scroogeUnpackDeps in Compile).value.flatMap { dir => (dir ** "*.thrift").get }
    }
  )
```

### Compile artifacts
```bash
$ sbt service/compile
```
