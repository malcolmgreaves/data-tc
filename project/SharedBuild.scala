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
    // Testing
    "io.netty"        %  "netty"              % "3.6.2.Final" % Test, // should be included /w 'spark-testing-base'
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


  lazy val pomExtraInfo = {
    <url>https://github.com/malcolmgreaves/data-tc</url>
    <licenses>
      <license>
        <name>Apache 2.0</name>
        <url>https://www.apache.org/licenses/LICENSE-2.0.txt</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:malcolmgreaves/data-tc.git</url>
      <connection>scm:git@github.com:malcolmgreaves/data-tc.git</connection>
    </scm>
    <developers>
      <developer>
        <id>malcolmgreaves</id>
        <name>Malcolm Greaves</name>
        <email>greaves.malcolm@gmail.com</email>
        <url>https://malcolmgreaves.io/</url>
      </developer>
    </developers>
 }

}