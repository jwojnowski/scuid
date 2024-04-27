// https://typelevel.org/sbt-typelevel/faq.html#what-is-a-base-version-anyway
ThisBuild / tlBaseVersion := "0.2" // your current series x.y

ThisBuild / organization     := "me.wojnowski"
ThisBuild / organizationName := "Jakub Wojnowski"
ThisBuild / startYear        := Some(2023)
ThisBuild / licenses         := Seq(License.MIT)
ThisBuild / developers       := List(
  // your GitHub handle and name
  tlGitHubDev("jwojnowski", "Jakub Wojnowski")
)

ThisBuild / tlSonatypeUseLegacyHost    := false
ThisBuild / tlCiReleaseBranches        := Seq("main")
ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec.temurin("21"))

val Scala213 = "2.13.13"
ThisBuild / crossScalaVersions := Seq(Scala213, "3.4.1")
ThisBuild / scalaVersion       := Scala213 // the default Scala

lazy val root = tlCrossRootProject.aggregate(core, circe, tapir)

lazy val core =
  project
    .in(file("core"))
    .settings(
      name := "scuid",
      libraryDependencies ++= Seq(
        "org.typelevel" %% "cats-core"               % "2.10.0",
        "org.typelevel" %% "cats-effect"             % "3.5.4",
        "org.scalameta" %% "munit"                   % "0.7.29" % Test,
        "org.scalameta" %% "munit-scalacheck"        % "0.7.29" % Test,
        "org.typelevel" %% "munit-cats-effect-3"     % "1.0.7"  % Test,
        "org.typelevel" %% "scalacheck-effect-munit" % "1.0.4"  % Test,
        "co.fs2"        %% "fs2-core"                % "3.10.2" % Test
      )
    )

lazy val circe =
  project
    .in(file("circe"))
    .dependsOn(core % "compile->compile;test->test")
    .settings(
      name := "scuid-circe",
      libraryDependencies ++= Seq(
        "io.circe"      %% "circe-core"       % "0.14.7",
        "io.circe"      %% "circe-literal"    % "0.14.7" % Test,
        "org.scalameta" %% "munit"            % "0.7.29" % Test,
        "org.scalameta" %% "munit-scalacheck" % "0.7.29" % Test
      )
    )

lazy val tapir =
  project
    .in(file("tapir"))
    .dependsOn(core % "compile->compile;test->test")
    .settings(
      name := "scuid-tapir",
      libraryDependencies ++= Seq(
        "com.softwaremill.sttp.tapir" %% "tapir-core" % "1.10.5"
      )
    )
