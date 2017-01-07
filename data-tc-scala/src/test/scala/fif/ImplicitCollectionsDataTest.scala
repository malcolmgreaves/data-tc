package fif

import org.scalatest.FunSuite

import scala.language.higherKinds

class ImplicitCollectionsDataTest extends FunSuite {

  import Data.ops._
  import ImplicitCollectionsData._

  test("implicit for Array") {
    simpleTest((1 until 10).toArray)
  }

  test("implicit for Seq") {
    simpleTest((1 until 10).toArray.toSeq)
  }

  test("implicit for Traversable") {
    simpleTest((1 until 10).toTraversable)
  }

  def simpleTest[D[_]: Data](data: D[Int]): Unit =
    data.foreach(x => assert(x > 0 && x < 10))

}
