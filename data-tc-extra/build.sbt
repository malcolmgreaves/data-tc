name := "data-tc-extra"

import SharedBuild._

com.typesafe.sbt.SbtScalariform.defaultScalariformSettings
ScalariformKeys.preferences := sharedCodeFmt

addCompilerPlugin(scalaMacros)

libraryDependencies ++= 
  extraDeps ++
  testDeps


// doc hacks

sources in (Compile, doc) ~= (_ filter (_.getName endsWith "ToMap.scala"))
sources in (Compile, doc) ~= (_ filter (_.getName endsWith "Sum.scala"))
sources in (Compile, doc) ~= (_ filter (_.getName endsWith "DataOps.scala"))

//
// test, runtime settings
//
fork in run               := true
fork in Test              := true
parallelExecution in Test := true

