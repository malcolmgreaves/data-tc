package com.nitro.absnlp

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.scala._
import org.scalatest.FunSuite

import scala.language.higherKinds
import scala.reflect.ClassTag
import fif.Data

class FlinkDataTest extends FunSuite {

  // for infix syntax
  import fif.Data.ops._

  // Data evidence for Flink DataSet
  implicit val t = FlinkData

  lazy val data: DataSet[Int] =
    ExecutionEnvironment.createLocalEnvironment(1).fromElements(1, 2, 3)

  def empty[T: ClassTag: TypeInformation]: DataSet[T] =
    ExecutionEnvironment.createLocalEnvironment(1).fromCollection(Seq.empty[T])

  test("test map") {

      def addElementwise10[D[_]: Data](data: D[Int]): D[Int] =
        data.map(_ + 10)

      def addElementwise10_tc[D[_]](data: D[Int])(implicit ev: Data[D]): D[Int] =
        ev.map(data)(_ + 10)

    {
      val changed = addElementwise10(data)

      assert(changed != data)
      assert(t.toSeq(changed) == List(11, 12, 13))
    }

    {
      val changed = addElementwise10_tc(data)

      assert(changed != data)
      assert(changed.collect() == Seq(11, 12, 13))
    }
  }

  test("mapPartition") {

      def mapParition10[D[_]: Data](data: D[Int]): D[Int] =
        data.mapParition { elements => elements.map(_ + 10) }

    val changed = mapParition10(data)
    assert(changed != data)
    assert(changed.collect() == Seq(11, 12, 13))
  }

  test("foreach") {

      def testForeach[D[_]: Data](data: D[Int]): Unit =
        data.foreach { x =>
          val res = x >= 1 && x <= 3
          if (!res) throw new RuntimeException
        }

    testForeach(data)
  }

  test("foreachPartition") {

      def testForeachPart[D[_]: Data](data: D[Int]): Unit =
        data.foreachPartition(_.foreach { x =>
          val res = x >= 1 && x <= 3
          if (!res) throw new RuntimeException
        })

    testForeachPart(data)
  }

  test("aggregate") {

      def aggregateTest[D[_]: Data](data: D[Int]): Int =
        data.aggregate(0)(_ + _, _ + _)

    assert(aggregateTest(data) == 6)
  }

  ignore("sortBy") {

      def reverseSort[D[_]: Data](data: D[Int]): D[Int] =
        data.sortBy(x => -x)

    assert(reverseSort(data).collect() == Seq(3, 2, 1))
  }

  test("take") {

      def testTake[D[_]: Data](data: D[Int]): Boolean =
        data.take(1) == Seq(1) && data.take(2) == Seq(1, 2) && data.take(3) == Seq(1, 2, 3)

    assert(testTake(data))
  }

  test("toSeq") {

      def testToSeqIs123[D[_]: Data](data: D[Int]): Boolean =
        data.toSeq == Seq(1, 2, 3)

    assert(testToSeqIs123(data))
  }

  test("flatMap") {

      def testFlat[D[_]: Data](data: D[Int]): D[Int] =
        data.flatMap { number =>
          (0 until number).map(_ => number)
        }

    val result = testFlat(data)
    assert(result.collect() == Seq(1, 2, 2, 3, 3, 3))
  }

  test("flatten") {

      def flattenTest[D[_]: Data](data: D[Seq[Int]]): D[Int] =
        data.flatten

    val expanded = data.map(x => Seq(x))
    val flattened = flattenTest(expanded)
    assert(flattened.collect() == data.collect())
  }

  test("groupBy") {

      def groupIt[D[_]: Data](data: D[Int]) = 
        data.groupBy { _ % 2 == 0 }

    val evenGroup = groupIt(data).toSeq.toMap

    val evens = evenGroup(true).toSet
    assert(evens.size == 1)
    assert(evens == Set(2))

    val odds = evenGroup(false).toSet
    assert(odds.size == 2)
    assert(odds == Set(1, 3))
  }

  test("size") {

      def sizeIs3[D[_]: Data](data: D[Int]): Boolean =
        data.size == 3

    assert(sizeIs3(data))
  }

  test("reduce") {

      def foo[D[_]: Data](data: D[Int]): Int =
        data.reduce {
          case (a, b) => 1 + a + b
        }

    val result = foo(data)
    assert(result == 8)
  }


  test("filter") {
      def f[D[_]: Data](data: D[Int]): D[Int] =
        data.filter(_ % 2 == 0)

    assert(f(data).collect() == Seq(2))
  }

  test("headOption") {
      def h[D[_]: Data](data: D[Int]): Option[Int] =
        data.headOption

    assert(h(data) == Some(1))
    assert(h(empty[Int]) == None)
  }

  test("isEmpty") {
      def e[D[_]: Data](data: D[_]): Boolean =
        data.isEmpty

    assert(!e(data))
    assert(e(empty[Int]))
  }

  // test("toMap") {
  //     def toM[D[_]: Data](data: D[Int]): Map[Int, Int] =
  //       fif.ops.ToMap(data.map(x => (x, x)))

  //   assert(toM(data) == Map(1 -> 1, 2 -> 2, 3 -> 3))
  // }


  // test("sum") {
  //     def s[D[_]: Data](data: D[Int]): Int =
  //       fif.ops.Sum(data)

  //   assert(s(data) == 6)
  // }

  ignore("zipWithIndex") {
      def foo[D[_]: Data](data: D[Int]): Unit =
        assert(data.zipWithIndex == Seq((1, 0), (2, 1), (3, 2)))

    foo(data)
  }

  ignore("zip") {
      def foo[D[_]: Data](data: D[Int]): D[(Int, Int)] =
        data.zip(data)

    assert(foo(data).collect() == Seq((1, 1), (2, 2), (3, 3)))
  }

}