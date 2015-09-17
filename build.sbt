lazy val commonSettings = Seq(
  organization := "com.memsql",
  version := "0.2.0",
  scalaVersion := "2.10.5",
  publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))
)

lazy val connectorLib = (project in file("connectorLib")).
  settings(commonSettings: _*).
  settings(
    name := "MemSQLRDD",
    libraryDependencies  ++= Seq(
      "org.apache.spark" %% "spark-core" % "1.4.1" % "provided",
      "org.apache.spark" %% "spark-sql" % "1.4.1"  % "provided",
      "mysql" % "mysql-connector-java" % "5.1.34"
    ),
    autoAPIMappings := true,
    apiMappings ++= {
      def findManagedDependency(organization: String, name: String): Option[File] = {
        (for {
          entry <- (fullClasspath in Runtime).value ++ (fullClasspath in Test).value
          module <- entry.get(moduleID.key) if module.organization == organization && module.name.startsWith(name)
        } yield entry.data).headOption
      }
      val links = Seq(
        findManagedDependency("org.apache.spark", "spark-core").map(d => d -> url("https://spark.apache.org/docs/1.4.1/api/scala/"))
      )
      links.collect { case Some(d) => d }.toMap
    }
  )

lazy val etlLib = (project in file("etlLib")).
  dependsOn(connectorLib).
  settings(commonSettings: _*).
  settings(
    name := "MemSQLETL",
    libraryDependencies  ++= Seq(
      "io.spray" %% "spray-json" % "1.3.2" % "provided",
      "org.apache.spark" %% "spark-streaming" % "1.4.1" % "provided",
      "org.apache.spark" %% "spark-sql" % "1.4.1"  % "provided",
      "org.scalatest" %% "scalatest" % "2.2.5" % "test"
    ),
    autoAPIMappings := true,
    apiMappings ++= {
      def findManagedDependency(organization: String, name: String): Option[File] = {
        (for {
          entry <- (fullClasspath in Runtime).value ++ (fullClasspath in Test).value
          module <- entry.get(moduleID.key) if module.organization == organization && module.name.startsWith(name)
        } yield entry.data).headOption
      }
      val links = Seq(
        findManagedDependency("org.apache.spark", "spark-core").map(d => d -> url("https://spark.apache.org/docs/1.4.1/api/scala/"))
      )
      links.collect { case Some(d) => d }.toMap
    }
  )

lazy val jarInspector = (project in file("jarInspector")).
  dependsOn(etlLib).
  settings(commonSettings: _*).
  settings(
    name := "jarInspector",
    libraryDependencies  ++= Seq(
      "org.apache.spark" %% "spark-core" % "1.4.1" % "provided",
      "io.spray" %% "spray-json" % "1.3.2",
      "com.github.scopt" %% "scopt" % "3.2.0",
      "org.reflections" % "reflections" % "0.9.10"
    )
  )

lazy val interface = (project in file("interface")).
  dependsOn(connectorLib).
  dependsOn(etlLib).
  dependsOn(jarInspector).
  settings(commonSettings: _*).
  settings(
    name := "MemSQLSparkInterface",
    parallelExecution in Test := false,
    libraryDependencies ++= {
      val akkaVersion = "2.3.9"
      val sprayVersion = "1.3.2"
      Seq(
        "io.spray" %% "spray-json" % sprayVersion,
        "io.spray" %% "spray-can" % sprayVersion,
        "io.spray" %% "spray-routing" % sprayVersion,
        "com.typesafe.akka" %% "akka-actor" % akkaVersion,
        "com.github.scopt" %% "scopt" % "3.2.0",
        "mysql" % "mysql-connector-java" % "5.1.34",
        "org.eclipse.jetty" % "jetty-servlet" % "8.1.14.v20131031" % "provided",
        "org.apache.spark" %% "spark-core" % "1.4.1" % "provided",
        "org.apache.spark" %% "spark-sql" % "1.4.1"  % "provided",
        "org.apache.spark" %% "spark-streaming" % "1.4.1" % "provided",
        "org.apache.spark" %% "spark-streaming-kafka" % "1.4.1" exclude("org.spark-project.spark", "unused"),
        "org.scalatest" %% "scalatest" % "2.2.5" % "test",
        "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
        "org.scalamock" %% "scalamock-scalatest-support" % "3.2" % "test",
        "io.spray" %% "spray-testkit" % sprayVersion % "test" exclude("org.scalamacros", "quasiquotes_2.10.3"),
        "org.apache.commons" % "commons-csv" % "1.2"
      )
    }
  )

lazy val examples = (project in file("examples")).
  dependsOn(interface).
  settings(commonSettings: _*).
  settings(
    name := "examples",
    libraryDependencies ++= {
      Seq(
        "mysql" % "mysql-connector-java" % "5.1.34",
        "org.apache.spark" %% "spark-core" % "1.4.1" % "provided",
        "org.apache.spark" %% "spark-streaming" % "1.4.1" % "provided",
        "org.apache.spark" %% "spark-sql" % "1.4.1"  % "provided"
      )
    }
  )

lazy val tests = (project in file("tests")).
  dependsOn(connectorLib).
  dependsOn(etlLib).
  dependsOn(interface).
  dependsOn(examples).
  settings(commonSettings: _*).
  settings(
    name := "tests",
    parallelExecution in Test := false,
    libraryDependencies ++= {
      Seq(
        "mysql" % "mysql-connector-java" % "5.1.34",
        "org.apache.spark" %% "spark-core" % "1.4.1" % "provided",
        "org.apache.spark" %% "spark-streaming" % "1.4.1" % "provided",
        "org.apache.spark" %% "spark-sql" % "1.4.1"  % "provided"
      )
    }
  )

lazy val root = (project in file(".")).
  dependsOn(connectorLib).
  dependsOn(etlLib).
  dependsOn(interface).
  settings(commonSettings: _*).
  settings(unidocSettings: _*).
  settings(
    name := "MemSQL",
    libraryDependencies  ++= Seq(
      "org.apache.spark" %% "spark-core" % "1.4.1" % "provided",
      "org.apache.spark" %% "spark-sql" % "1.4.1"  % "provided",
      "org.apache.spark" %% "spark-streaming" % "1.4.1" % "provided",
      "mysql" % "mysql-connector-java" % "5.1.34"
    )
  )
