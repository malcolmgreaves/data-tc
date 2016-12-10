data-tc-spark
==============

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.malcolmgreaves/data-tc-spark_2.11/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/io.malcolmgreaves/data-tc-spark_2.11)

An implementation of the `Data` type class using Spark `RDD`s (http://bit.ly/1MAZ7mj).

#### Want evidence that an `RDD` satisfies the `Data` typeclass?
To make an `RDD` adhere to the `Data` type class, be sure to have the following imported in your scope:

   import datatc.spark._

For example, if using the following trivial `product` method:

    import datatc.Data
    
    def product[D[_] : Data](data: D[Double]): Double = {
       import Data.ops._
       data.reduce(_ * _)
    }

on an `RDD` of `Double`s named `nums`, we would write the following:

    val nums: RDD[Double] = ...
    import datatc.spark._
    val p = product(nums)
    println(s"The product of the ${nums.size} numbers is $p")

### How do I use this in my project?

Add the following to your `build.sbt`

    libraryDependencies ++= Seq("io.malcolmgreaves" %% "data-tc-spark" % "X.Y.Z")

Where `X.Y.Z` is the latest release.

### License

Copyright 2015 Malcolm Greaves

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

