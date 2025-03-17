ThisBuild / version := "0.1.0-SNAPSHOT"
lazy val root = (project in file("."))
  .settings(
    name := "asmd23-02-testing",
      libraryDependencies ++= Seq(
          "net.aichler" % "jupiter-interface" % JupiterKeys.jupiterVersion.value % Test,
          "org.mockito" % "mockito-core" % "5.16.1" % Test)
)
