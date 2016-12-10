package fif.ops

import fif.TravData
import fif.TestHelpers._
import org.scalatest.FunSuite

class SumTest extends FunSuite {

  implicit val t = TravData

  test("sum empty list") {
    assert(Sum(Traversable.empty[Int]) == 0)
  }

  test("sum list of one element") {
    val l = Traversable(10)
    assert(Sum(l) == 10)
  }

  test("sum list of many elements") {
    val l = Traversable(10, 20, 30, 40, 50)
    assert(Sum(l) == 150)
  }

  test("sum variadic numbers") {
    assert(Sum(1, 2, 3, 4, 5, 6) == 21)
  }

}

