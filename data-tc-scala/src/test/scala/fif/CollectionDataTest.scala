package fif

import org.scalatest.FunSuite

import scala.language.higherKinds

protected trait CollectionDataTest[D[_]] extends FunSuite {
  // for infix syntax
  import Data.ops._

  implicit def dataTypeClass: Data[D]

  val data: D[Int]
  val empty: D[Int]

  test("test map") {

    def addElementwise10(data: D[Int]): D[Int] =
      data.map(_ + 10)

    val changed = addElementwise10(data)

    assert(changed.toSeq !== data)
    assert(changed.toSeq === Seq(11, 12, 13))
  }

  test("mapPartition") {

    def mapParition10(data: D[Int]): D[Int] =
      data.mapParition { elements => elements.map(_ + 10) }

    val changed = mapParition10(data)
    assert(changed.toSeq !== data)
    assert(changed.toSeq === Seq(11, 12, 13))
  }

  test("foreach") {

    def testForeach(data: D[Int]): Unit =
      data.foreach(x => assert(x >= 1 && x <= 3))

    testForeach(data)
  }

  test("foreachPartition") {

    def testForeachPart(data: D[Int]): Unit =
      data.foreachPartition(_.foreach(x => assert(x >= 1 && x <= 3)))

    testForeachPart(data)
  }

  test("aggregate") {

    def aggregateTest(data: D[Int]): Int =
      data.aggregate(0)(_ + _, _ + _)

    assert(aggregateTest(data) === 6)
  }

  test("sortBy") {

    def reverseSort(data: D[Int]): D[Int] =
      data.sortBy(x => -x)

    assert(reverseSort(data).toSeq === Seq(3, 2, 1))
  }

  test("take") {

    def testTake(data: D[Int]): Boolean =
      data.take(1) == Seq(1) && data.take(2) == Seq(1, 2) && data.take(3) == Seq(1, 2, 3)

    assert(testTake(data))
    assert(data.take(0).toSeq === Seq.empty[Int])
  }

  test("toSeq") {

    def testToSeqIs123(data: D[Int]): Boolean =
      data.toSeq == Seq(1, 2, 3)

    assert(testToSeqIs123(data))
  }

  test("flatMap") {

    def testFlat(data: D[Int]): D[Int] =
      data.flatMap { number =>
        (0 until number).map(_ => number)
      }

    val result = testFlat(data)
    assert(result === Seq(1, 2, 2, 3, 3, 3))
  }

  test("flatten") {

    def flattenTest(data: D[Seq[Int]]): D[Int] =
      data.flatten

    val expanded = data.map(x => Seq(x))
    val flattened = flattenTest(expanded)
    assert(flattened === data)
  }

  test("groupBy") {

    def groupIt(data: D[Int]): D[(Boolean, Iterable[Int])] =
      data.groupBy { _ % 2 == 0 }

    val evenGroup = groupIt(data).toSeq.toMap

    val evens = evenGroup(true).toSet
    assert(evens.size === 1)
    assert(evens === Set(2))

    val odds = evenGroup(false).toSet
    assert(odds.size === 2)
    assert(odds === Set(1, 3))
  }

  test("size") {

    def sizeIs3(data: D[Int]): Boolean =
      data.size == 3

    assert(sizeIs3(data))
  }

  test("reduce") {

    def foo(data: D[Int]): Int =
      data.reduce {
        case (a, b) => 1 + a + b
      }

    val result = foo(data)
    assert(result === 8)
  }

  test("filter") {
    def f(data: D[Int]): D[Int] =
      data.filter(_ % 2 == 0)

    assert(f(data).toSeq === Seq(2))
  }

  test("headOption") {
    def h(data: D[Int]): Option[Int] =
      data.headOption

    assert(h(data) === Some(1))
    assert(h(empty) === None)
  }

  test("isEmpty") {
    def e(data: D[_]): Boolean =
      data.isEmpty

    assert(!e(data))
    assert(e(empty))
  }

  test("zipWithIndex") {
    def foo(data: D[Int]): Unit =
      assert(data.zipWithIndex.toSeq === Seq((1, 0), (2, 1), (3, 2)))

    foo(data)
  }

  test("zip") {
    def foo(data: D[Int]): D[(Int, Int)] =
      data.zip(data)

    assert(foo(data).toSeq === Seq((1, 1), (2, 2), (3, 3)))
  }

}