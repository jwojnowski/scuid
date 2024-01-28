// https://typelevel.org/sbt-typelevel/faq.html#what-is-a-base-version-anyway
ThisBuild / tlBaseVersion := "0.1" // your current series x.y

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

// publish website from this branch
ThisBuild / tlSitePublishBranch := Some("main")

val Scala213 = "2.13.12"
ThisBuild / crossScalaVersions := Seq(Scala213, "3.3.1")
ThisBuild / scalaVersion       := Scala213 // the default Scala

lazy val root = tlCrossRootProject.aggregate(core)

lazy val core =
  project
    .in(file("core"))
    .settings(
      name := "scuid",
      libraryDependencies ++= Seq(
        "org.typelevel" %% "cats-core"               % "2.10.0",
        "org.typelevel" %% "cats-effect"             % "3.5.3",
        "org.scalameta" %% "munit"                   % "0.7.29" % Test,
        "org.scalameta" %% "munit-scalacheck"        % "0.7.29" % Test,
        "org.typelevel" %% "munit-cats-effect-3"     % "1.0.7"  % Test,
        "org.typelevel" %% "scalacheck-effect-munit" % "1.0.4"  % Test,
        "co.fs2"        %% "fs2-core"                % "3.7.0"  % Test
      )
    )
