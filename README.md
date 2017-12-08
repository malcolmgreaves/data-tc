# data-tc

[![Build Status](https://circleci.com/gh/malcolmgreaves/data-tc.svg?style=shield&circle-token=:circle-token)](https://circleci.com/gh/malcolmgreaves/data-tc) [![Coverage Status](https://coveralls.io/repos/malcolmgreaves/data-tc/badge.svg?branch=master&service=github)](https://coveralls.io/github/malcolmgreaves/data-tc?branch=master)
 [![Codacy Badge](http://api.codacy.com:80/project/badge/7a4fbaf2cbe6449993224d6eb4df0f13)](https://www.codacy.com/app/greavesmalcolm/data-tc) [![Stories in Ready](https://badge.waffle.io/malcolmgreaves/data-tc.png?label=ready&title=Ready)](https://waffle.io/malcolmgreaves/data-tc)  [![Join the chat at https://gitter.im/malcolmgreaves/data-tc](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/malcolmgreaves/data-tc?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.malcolmgreaves/data-tc-scala_2.11/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/io.malcolmgreaves/data-tc-scala_2.11)

A unifying typeclass describing collections and higher-order data transformation and manipulation actions common to a wide variety of data processing tasks. Inspired by the Scala collections API.

### Why Use `data-tc`?

Write an algorithm that accepts a type that adheres to the [Data](https://github.com/malcolmgreaves/data-tc/blob/master/data-tc-scala/src/main/scala/datatc/Data.scala) type class and watch it work everywhere! `Data` describes higher-order-functions that manipulate and transform generic collections. It makes use of the [typeclass](https://en.wikipedia.org/wiki/Type_class) design pattern for ad-hoc polymorphism. This strategy stands in contrast to the common inheritence-based sub-typing polymorphism that is familiar in nearly every OO langauge. Provuding duck typing like developer productivity with the safety of strong static type checking, typeclasses are a powerful and incredibly flexible mechanism for describing generic behaviors.

Implementations for concrete types of the `Data` typeclass include:
* [Scala Collections (Seq, Traversable, Map, Set, etc.)](https://github.com/malcolmgreaves/data-tc/tree/master/data-tc-scala)
* [Spark RDD](https://github.com/malcolmgreaves/data-tc/tree/master/data-tc-spark)
* [Flink DataSet](https://github.com/malcolmgreaves/data-tc/tree/master/data-tc-flink)

### Installation

Add the following to your `build.sbt` file:

    libraryDependencies ++= Seq("io.malcolmgreaves" %% "data-tc-{scala,spark,fink,extra}" % "X.Y.Z")

Where `X.Y.Z` is the most recent one from [sonatype](https://oss.sonatype.org/content/repositories/releases/io/malcolmgreaves/data-tc-scala_2.11/).

### Examples

We strive for high code coverage. Check out all of the tests:
* [Scala Collection tests](https://github.com/malcolmgreaves/data-tc/tree/master/data-tc-scala/src/test/scala/datatc/spark)
* [Spark RDD tests](https://github.com/malcolmgreaves/data-tc/tree/master/data-tc-spark/src/test/scala/datatc/spark)
* [Flink DataSet tests](https://github.com/malcolmgreaves/data-tc/tree/master/data-tc-flink/src/test/scala/datatc/spark)

For a rather small use case, check out `Sum()`, which shows how to implement the common `sum` functionality on a `Data` type class instance with `Numeric` elements:

```scala
object Sum extends Serializable {
    
  import datatc.Data
  // Brings implicits in scope for things like `map`, `flatMap`, etc.
  // as object oriented style infix notation. These are still using
  // the type class method definitions!
  import Data.ops._
      
  def apply[N: Numeric: ClassTag, D[_]: Data](data: D[N]): N = {
    val add = implicitly[Numeric[N]].plus _
    data.aggregate(implicitly[Numeric[N]].zero)(add, add)
  }

  def apply[N: Numeric](first: N, numbers: N*): N = {
    val add = implicitly[Numeric[N]].plus _
    numbers.foldLeft(first)(add)
  }
}
```

With this `Sum` object, we can perform a summation over a `Traversable` instance:

```scala
// Brings into scope all Data typeclass evidence for Scala collections.
import datatc.scala._
Sum(Traversable(1.0, 2.0, 3.0)) == 6.0
Sum(1, 2, 3) == 6
```

### Repository Structure
This project is organized into several sbt sub-projects:

* [data-tc-scala](https://github.com/malcolmgreaves/data-tc/tree/master/data-tc-scala)
  * Typeclass definition as `datatc.Data`
  * Implementations using Scala collections under `datatc.scala._`
  * Only depends on Scala standard library
  
* [data-tc-spark](https://github.com/malcolmgreaves/data-tc/tree/master/data-tc-spark)
  * Implementation using Spark RDD under `datatc.spark._`
  
* [data-tc-flink](https://github.com/malcolmgreaves/data-tc/tree/master/data-tc-flink)
  * Implementation using Flink DataSet udner `datatc.flink._`
* [data-tc-extra](https://github.com/malcolmgreaves/data-tc/tree/master/data-tc-extra)
  * Additional functionality using the `Data` typeclass with 3rd party libraries. 

### Contributing
We <3 contributions! We want this code to be useful and used! We use pull requests to review and discuss changes, fixes, and improvements.

### License

Copyright 2015-2018 Malcolm Greaves

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
