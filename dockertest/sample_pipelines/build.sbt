resolvers += "memsql-internal" at "http://coreos-10.memcompute.com:8080/repository/internal"

lazy val root = (project in file(".")).
  settings(
    name := "sample-pipelines",
    version := "0.0.1",
    scalaVersion := "2.10.5",
    libraryDependencies  ++= Seq(
        "org.apache.spark" %% "spark-core" % "1.5.0" % "provided",
        "org.apache.spark" %% "spark-sql" % "1.5.0"  % "provided",
        "org.apache.spark" %% "spark-streaming" % "1.5.0" % "provided",
        "com.memsql" %% "memsqletl" % "0.1.9" % "provided"
    )
)
