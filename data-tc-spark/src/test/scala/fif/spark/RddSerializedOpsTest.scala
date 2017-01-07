package fif.spark

import com.holdenkarau.spark.testing.SharedSparkContext
import org.scalatest.FunSuite

/**
  * Tests SparkModule higher-order-functions.
  */
class RddSerializedOpsTest extends FunSuite with SharedSparkContext {

  import RddSerializedOpsTest._

  /**
    * In class (vs. companion object) so that we have access to `assert` from `FunSuite`.
    */
  private def checkResults[A](correct: Seq[A], results: Seq[A]): Unit =
    correct.zip(results).foreach {
      case (c, r) =>
        assert(c === r)
    }

  test("Map") {
    val f = (s: String) => s"${s}_$s"
    val correct = dumbData.map(f)
    val mapper = RddSerializedOps.Map(f)
    val results = mapper(sc.parallelize(dumbData)).collect().toSeq

    checkResults(correct, results)
  }

  test("FlatMap") {
    val f = (s: String) => Seq.fill(10)(s)
    val correct = dumbData.flatMap(f)
    val flatMapper = RddSerializedOps.FlatMap(f)
    val results = flatMapper(sc.parallelize(dumbData)).collect().toSeq

    checkResults(correct, results)
  }

  test("Foreach") {
    val f = (s: String) =>
      if (!(dumbDataSet contains s))
        throw new RuntimeException(s"unexpected input: $s")
      else
      Unit
    val foreacher = RddSerializedOps.Foreach(f)

    foreacher(sc.parallelize(dumbData))
  }

  test("Aggregate") {
    val aggregator =
      RddSerializedOps.Aggregate(
        Set.empty[String],
        (s: Set[String], x: String) => s + x,
        (s1: Set[String], s2: Set[String]) => s1 ++ s2
      )
    val result = aggregator(sc.parallelize(dumbData))

    assert(result === dumbDataSet)
  }

}

object RddSerializedOpsTest {

  val dumbData = Seq("hello", "world", "how", "are", "you", "today")

  val dumbDataSet = dumbData.toSet

}
