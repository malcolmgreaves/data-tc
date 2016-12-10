package fif

import com.holdenkarau.spark.testing.SharedSparkContext
import org.apache.spark.{ SparkConf, SparkContext }
import org.apache.spark.rdd.RDD
import org.scalatest.{ BeforeAndAfterAll, FunSuite }

import scala.language.higherKinds
import scala.reflect.ClassTag

// for infix syntax when working with things of the Data type class
import Data.ops._
// Data evidence for Spark RDD
import fif.ImplicitRddData._

class RddDataTest extends FunSuite with SharedSparkContext with Serializable {

  lazy val data: RDD[Int] =
    sc.parallelize(Seq(1, 2, 3))

  def empty[T: ClassTag]: RDD[T] =
    sc.parallelize(Seq.empty[T])

  test("test map") {

    def addElementwise10[D[_]: Data](data: D[Int]): D[Int] =
      data.map(_ + 10)

    def addElementwise10_tc[D[_]](data: D[Int])(implicit ev: Data[D]): D[Int] =
      ev.map(data)(_ + 10)

    {
      val changed = addElementwise10(data)

      assert(changed !== data)
      assert(implicitly[Data[RDD]].toSeq(changed) === List(11, 12, 13))
    }

    {
      val changed = addElementwise10_tc(data)

      assert(changed !== data)
      assert(changed.collect().toSeq === Seq(11, 12, 13))
    }
  }

  test("mapPartition") {

    def mapParition10[D[_]: Data](data: D[Int]): D[Int] =
      data.mapParition { elements => elements.map(_ + 10) }

    val changed = mapParition10(data)
    assert(changed !== data)
    assert(changed.collect().toSeq === Seq(11, 12, 13))
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

    assert(aggregateTest(data) === 6)
  }

  test("sortBy") {

    def reverseSort[D[_]: Data](data: D[Int]): D[Int] =
      data.sortBy(x => -x)

    assert(reverseSort(data).collect().toSeq === Seq(3, 2, 1))
  }

  test("take") {

    def testTake[D[_]: Data](data: D[Int]): Boolean =
      data.take(1) == Seq(1) && data.take(2) == Seq(1, 2) && data.take(3) == Seq(1, 2, 3)

    assert(testTake(data))
  }

  test("toSeq") {

    def testToSeqIs123[D[_]: Data](data: D[Int]): Unit =
      assert(data.toSeq === Seq(1, 2, 3))

    testToSeqIs123(data)
  }

  test("flatMap") {

    def testFlat[D[_]: Data](data: D[Int]): D[Int] =
      data.flatMap { number =>
        (0 until number).map(_ => number)
      }

    val result = testFlat(data)
    assert(result.collect().toSeq === Seq(1, 2, 2, 3, 3, 3))
  }

  test("flatten") {

    def flattenTest[D[_]: Data](data: D[Seq[Int]]): D[Int] =
      data.flatten

    val expanded = data.map(x => Seq(x))
    val flattened = flattenTest(expanded)
    assert(flattened.collect().toSeq === data.collect().toSeq)
  }

  test("groupBy") {

    def groupIt[D[_]: Data](data: D[Int]): D[(Boolean, Iterable[Int])] =
      data.groupBy { n => n % 2 == 0 }

    val evenGroup = groupIt(data).toSeq.toMap

    val evens = evenGroup(true).toSet
    assert(evens.size === 1)
    assert(evens === Set(2))

    val odds = evenGroup(false).toSet
    assert(odds.size === 2)
    assert(odds === Set(1, 3))
  }

  test("size") {

    def sizeIs3[D[_]: Data](data: D[Int]): Unit =
      assert(data.size === 3)

    sizeIs3(data)
  }

  test("reduce") {

    def foo[D[_]: Data](data: D[Int]): Int =
      data.reduce {
        case (a, b) => 1 + a + b
      }

    val result = foo(data)
    assert(result === 8)
  }

  test("filter") {
    def f[D[_]: Data](data: D[Int]): D[Int] =
      data.filter(_ % 2 == 0)

    assert(f(data).collect().toSeq === Seq(2))
  }

  test("headOption") {
    def h[D[_]: Data](data: D[Int]): Option[Int] =
      data.headOption

    assert(h(data) === Some(1))
    assert(h(empty[Int]) === None)
  }

  test("isEmpty") {
    def e[D[_]: Data](data: D[_]): Boolean =
      data.isEmpty

    assert(!e(data))
    assert(e(empty[Int]))
  }

  test("zipWithIndex") {
    def foo[D[_]: Data](data: D[Int]): Unit =
      assert(data.zipWithIndex.toSeq === Seq((1, 0), (2, 1), (3, 2)))

    foo(data)
  }

  test("zip") {
    def foo[D[_]: Data](data: D[Int]): D[(Int, Int)] =
      data.zip(data)

    assert(foo(data).collect().toSeq === Seq((1, 1), (2, 2), (3, 3)))
  }

  // test("sum") {
  //   assert(Sum(data) == 6)
  // }

  // test("toMap") {
  //   assert(ToMap(data.map(x => (x, x))) === Map(1 -> 1, 2 -> 2, 3 -> 3))
  // }

}