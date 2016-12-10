package fif.ops

import fif.TravData
import org.scalatest.FunSuite
import fif.TestHelpers._

class ToMapTest extends FunSuite {

  implicit val t = TravData

  test("ToMap empty list") {
    assert(ToMap(Traversable.empty[(String, Int)]) == Map.empty[String, Int])
  }

  test("ToMap list of one element") {
    val l = Traversable(("Hello", 10))
    assert(ToMap(l) == Map("Hello" -> 10))
  }

  test("ToMap list of many elements") {
    val l = Traversable(
      ("hello", 10), ("hello", 20),
      ("world", 30),
      ("sunday funday", 40),
      ("sunday funday", 50),
      ("hello", 42)
    )
    val expected = Map(
      ("hello", 72),
      ("world", 30),
      ("sunday funday", 90)
    )
    assert(ToMap(l) == expected)
  }

  test("combine to maps") {
    val first = Map(
      ("hello", 72),
      ("world", 30),
      ("sunday funday", 90),
      ("only in first", 1)
    )
    val second = Map(
      ("hello", 28),
      ("sunday funday", 110),
      ("only in second", 2)
    )
    val expected = Map(
      ("hello", 100),
      ("world", 30),
      ("sunday funday", 200),
      ("only in first", 1),
      ("only in second", 2)
    )

    assert(ToMap.combine(first, second) == expected)
  }

}