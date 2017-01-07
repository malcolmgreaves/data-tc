name := "data-tc-flink"

import SharedBuild._

addCompilerPlugin(scalaMacros)

libraryDependencies ++=
  flinkTcDeps ++
    testDeps

testOptions in Test += Tests.Argument(TestFrameworks.JUnit, "-v")
testOptions in Test += Tests.Argument("-oF")
fork in Test := true
parallelExecution in Test := true

pomExtra := pomExtraInfo
