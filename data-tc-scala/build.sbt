name := "data-tc-scala"

import SharedBuild._

com.typesafe.sbt.SbtScalariform.defaultScalariformSettings
ScalariformKeys.preferences := sharedCodeFmt

addCompilerPlugin(scalaMacros)

libraryDependencies ++= 
  scalaTcDeps ++
  testDeps

//
// test, runtime settings
//
fork in run               := true
fork in Test              := true
parallelExecution in Test := true

