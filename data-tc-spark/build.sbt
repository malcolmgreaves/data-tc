name := "data-tc-spark"

import SharedBuild._

// >>=
scalacOptions := {
  val badOptionsWhenUsingSpark151 = Set("-Yopt:_")
  scalacOptions.value.filter { opt =>
    !badOptionsWhenUsingSpark151.contains(opt)
  }
}

addCompilerPlugin(scalaMacros)

libraryDependencies ++=
  sparkTcDeps ++
    testDeps

// test & misc. configuration
//
fork in Test := false
parallelExecution in Test := false
fork in run := false

pomExtra := pomExtraInfo
