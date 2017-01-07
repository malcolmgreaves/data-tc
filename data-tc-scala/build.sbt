name := "data-tc-scala"

import SharedBuild._

addCompilerPlugin(scalaMacros)

libraryDependencies ++=
  scalaTcDeps ++
    testDeps

//
// test, runtime settings
//
fork in run := true
fork in Test := true
parallelExecution in Test := true

pomExtra := pomExtraInfo
