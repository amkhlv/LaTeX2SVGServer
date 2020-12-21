val Http4sVersion = "0.21.11"
val CirceVersion = "0.13.0"
val Specs2Version = "4.10.5"
val LogbackVersion = "1.2.3"

lazy val root = (project in file("."))
  .settings(
    organization := "com.andreimikhailov",
    name := "latex2svgserver",
    version := "1.0",
    scalaVersion := "2.13.3",
    libraryDependencies ++= Seq(
      "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"      %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "io.circe"        %% "circe-generic"       % CirceVersion,
      "org.specs2"      %% "specs2-core"         % Specs2Version % "test",
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion,
      "org.scalameta"   %% "svm-subs"            % "20.2.0",
      "org.scilab.forge" % "jlatexmath"          % "1.0.7",
      "org.apache.xmlgraphics" % "batik-svggen" % "1.13",
      "org.apache.xmlgraphics" % "batik-dom" % "1.13",
      "org.jbibtex"     % "jbibtex"              % "1.0.15",
      "io.circe"        %% "circe-generic"       % CirceVersion,
      "io.circe"        %% "circe-yaml"          % CirceVersion,
      "io.circe"        %% "circe-parser"        % CirceVersion,
      "com.typesafe"    % "config"               % "1.4.1",
      "org.scala-lang.modules" %% "scala-xml" % "1.2.0",
      "com.google.code.findbugs" % "jsr305" % "3.0.2" % Optional
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3"),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
    test in assembly := {},
    assemblyOutputPath in assembly := new File("./latex2svgserver.jar"),
    assemblyMergeStrategy in assembly := {
      case "application.conf" => MergeStrategy.concat
      case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
      case x => MergeStrategy.first
    }
  )
