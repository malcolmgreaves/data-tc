import sbt._
import Keys._

object SharedBuild {

  // // // // // // // // // //
  // //     Versions      // //
  // // // // // // // // // //

  lazy val sparkVer     = "2.0.2"
  lazy val parquetVer   = "1.6.0"
  lazy val flinkVer     = "1.1.0"

  // // // // // // // // // //
  // //    Dependencies   // //
  // // // // // // // // // //

  lazy val testDeps = Seq(
    "org.scalatest" %% "scalatest" % "2.2.6" % Test
  )

  lazy val scalaTcDeps = Seq( 
    "com.github.mpilquist" %% "simulacrum" % "0.7.0"
  )

  lazy val flinkTcDeps = Seq(
    "org.apache.flink" %% "flink-scala"   % flinkVer,
    "org.apache.flink" %% "flink-clients" % flinkVer
  )

  lazy val sparkTcDeps = Seq(
    // avro codegen
    "com.gonitro" %% "avro-codegen-runtime" % "0.3.4",
    // spark & parquet
    "org.apache.spark" %% "spark-core"      % sparkVer,
    "com.twitter"      %  "parquet-avro"    % parquetVer,
    "com.twitter"      %  "parquet-hadoop"  % parquetVer,
    "com.twitter"      %  "parquet-column"  % parquetVer,
    "io.netty" % "netty" % "3.6.2.Final",
    // Testing
    "com.holdenkarau" %% "spark-testing-base" % s"${sparkVer}_0.4.7" % Test
  )

  lazy val extraDeps = Seq(
    "org.spire-math" %% "algebra" % "0.3.1"
  )

  // // // // // // // // // //
  // //      Plugins      // //
  // // // // // // // // // //

  lazy val scalaMacros =
    "org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full

  //////////////////////////////////////////////////
  //   Code formatting settings for scalariform   //
  //////////////////////////////////////////////////

  lazy val sharedCodeFmt = {
    import scalariform.formatter.preferences._
    FormattingPreferences()
      .setPreference(AlignParameters,                            true  )
      .setPreference(AlignSingleLineCaseStatements,              true  )
      .setPreference(CompactControlReadability,                  false )
      .setPreference(CompactStringConcatenation,                 true  )
      .setPreference(DoubleIndentClassDeclaration,               true  )
      .setPreference(FormatXml,                                  true  )
      .setPreference(IndentLocalDefs,                            true  )
      .setPreference(IndentPackageBlocks,                        true  )
      .setPreference(IndentSpaces,                               2     )
      .setPreference(MultilineScaladocCommentsStartOnFirstLine,  false )
      .setPreference(PreserveDanglingCloseParenthesis,           true  )
      .setPreference(PreserveSpaceBeforeArguments,               false )
      .setPreference(RewriteArrowSymbols,                        false )
      .setPreference(SpaceBeforeColon,                           false )
      .setPreference(SpaceInsideBrackets,                        false )
      .setPreference(SpacesWithinPatternBinders,                 true  )
  }

}
