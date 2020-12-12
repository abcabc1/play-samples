
lazy val root = (project in file("."))
  .enablePlugins(PlayJava, PlayEbean)
  .settings(
    name := """play-java-starter-example""",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq(
      guice,
      javaForms,
      // Test Database
      "com.h2database" % "h2" % "1.4.199",
      // Testing libraries for dealing with CompletionStage...
      "mysql" % "mysql-connector-java" % "5.1.47",
      javaJdbc,
      javaWs,
      "org.beanshell" % "bsh" % "2.0b5",
      "com.github.stuxuhai" % "jpinyin" % "1.1.8",
      "net.coobird" % "thumbnailator" % "0.4.8",
      "org.jsoup" % "jsoup" % "1.12.1",
      "io.jsonwebtoken" % "jjwt" % "0.9.1",
      "com.alibaba" % "easyexcel" % "2.2.3",
      // https://mvnrepository.com/artifact/javax.el/javax.el-api
//      "javax.el" % "javax.el-api" % "3.0.0",
      "org.glassfish" % "jakarta.el" % "3.0.3",
      "me.xdrop" % "fuzzywuzzy" % "1.2.0",
      "org.elasticsearch.client" % "elasticsearch-rest-high-level-client" % "6.8.1",
      "org.elasticsearch" % "elasticsearch" % "6.8.1",
      "org.assertj" % "assertj-core" % "3.14.0" % Test,
      "org.awaitility" % "awaitility" % "4.0.1" % Test,
      "org.mockito" % "mockito-core" % "3.0.0" % Test,
    ),
    javacOptions ++= Seq(
      "-encoding", "UTF-8",
      "-parameters",
      "-Xlint:unchecked",
      "-Xlint:deprecation",
      "-Werror"
    ),
    // Make verbose tests
    testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))
  )
