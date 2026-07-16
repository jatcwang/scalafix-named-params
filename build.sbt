import sbtghactions.JavaSpec

lazy val V = _root_.scalafix.sbt.BuildInfo

val scala3 = "3.8.4"

inThisBuild(
  List(
    organization := "com.github.jatcwang",
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    homepage := Some(url("https://github.com/jatcwang/scalafix-named-params")),
    developers := List(
      Developer(
        "jatcwang",
        "Jacob Wang",
        "jatcwang@gmail.com",
        url("https://almostfunctional.com")
      )
    ),
    scalaVersion := V.scala213,
    crossScalaVersions := Seq(V.scala213),
  )
)

publish / skip := true

lazy val semanticdbSetting = Def.settings(
  libraryDependencies ++= {
    scalaBinaryVersion.value match {
      case "3" =>
        Nil
      case _ =>
        Seq(compilerPlugin(scalafixSemanticdb))
    }
  },
  scalacOptions ++= {
    scalaBinaryVersion.value match {
      case "3" =>
        List(
          "-Xsemanticdb"
        )
      case _ =>
        List(
          "-Yrangepos",
          "-P:semanticdb:synthetics:on"
        )
    }
  },
)

lazy val rules = project.settings(
  moduleName := "scalafix-named-params",
  libraryDependencies += "ch.epfl.scala" %% "scalafix-core" % V.scalafixVersion
)

lazy val input = project.settings(
  publish / skip := true,
  semanticdbSetting,
  crossScalaVersions += scala3
)

lazy val output = project.settings(
  publish / skip := true,
  semanticdbSetting,
  crossScalaVersions += scala3
)

lazy val tests = project
  .settings(
    publish / skip := true,
    semanticdbSetting,
    libraryDependencies += "ch.epfl.scala" % "scalafix-testkit" % V.scalafixVersion % Test cross CrossVersion.full,
    scalafixTestkitInputScalacOptions :=
      (input / Compile / scalacOptions).value,
    scalafixTestkitInputScalaVersion :=
      (input / Compile / scalaVersion).value,
    scalafixTestkitOutputSourceDirectories :=
      (output / Compile / sourceDirectories).value,
    scalafixTestkitInputSourceDirectories :=
      (input / Compile / sourceDirectories).value,
    scalafixTestkitInputClasspath :=
      (input / Compile / fullClasspath).value
  )
  .dependsOn(rules)
  .enablePlugins(ScalafixTestkitPlugin)

lazy val setupMise = Seq(
  WorkflowStep.Use(
    UseRef.Public("jdx", "mise-action", "v4"),
    name = Some("Setup mise"),
  ),
)

ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec.temurin("17"))
ThisBuild / githubWorkflowScalaVersions := Seq(V.scala213, scala3)
ThisBuild / githubWorkflowTargetTags := Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches :=
  Seq(RefPredicate.StartsWith(Ref.Tag("v")))

ThisBuild / githubWorkflowJobSetup :=
  Seq(WorkflowStep.CheckoutFull) ++
    setupMise ++
    Seq(WorkflowStep.SetupSbt()) ++
    githubWorkflowGeneratedCacheSteps.value

ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Sbt(
    List("ci-release"),
    env = Map(
      "PGP_PASSPHRASE" -> "${{ secrets.PGP_PASSPHRASE }}",
      "PGP_SECRET" -> "${{ secrets.PGP_SECRET }}",
      "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
      "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
    )
  )
)

// Filter out MacOS and Windows cache steps to make yaml less noisy
ThisBuild / githubWorkflowGeneratedCacheSteps ~= { currentSteps =>
  currentSteps.filterNot(wf => wf.cond.exists(str => str.contains("macos") || str.contains("windows")))
}
