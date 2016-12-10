data-tc-extra
=============
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.malcolmgreaves/data-tc-extra_2.11/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/io.malcolmgreaves/data-tc-extra_2.11)

Additional functionality for `Data` typeclasses using 3rd party libraries. Currently, the following actions are implemented:
* `Sum`
  * Sum values of a `Data` instance whose elements belong to a `Semigroup`.
* `ToMap`
  * Convert a `Data` instance of 2-tuple elements into a `Map`.