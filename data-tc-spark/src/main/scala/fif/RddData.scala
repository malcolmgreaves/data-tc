package fif

import org.apache.spark.rdd.RDD

import scala.language.{ higherKinds, implicitConversions }
import scala.reflect.ClassTag
import scala.util.Try

object RddData extends Data[RDD] with Serializable {

  /** Transform a dataset by applying f to each element. */
  override def map[A, B: ClassTag](data: RDD[A])(f: (A) => B): RDD[B] =
    data.map(f)

  override def mapParition[A, B: ClassTag](d: RDD[A])(f: Iterable[A] => Iterable[B]): RDD[B] =
    d.mapPartitions { partition =>
      f(partition.toIterable).toIterator
    }

  /** Apply a side-effecting function to each element. */
  override def foreach[A](d: RDD[A])(f: A => Any): Unit =
    d.foreach { x =>
      // RDD's foreach requires A => Unit, so we do a discarding assignment
      val _ = f(x)
    }

  override def foreachPartition[A](d: RDD[A])(f: Iterable[A] => Any): Unit =
    d.foreachPartition { partition =>
      // RDD's foreachPartition requires A => Unit, so we do this assignment
      val _ = f(partition.toIterable)
    }

  override def filter[A](d: RDD[A])(f: A => Boolean): RDD[A] =
    d.filter(f)

  override def aggregate[A, B: ClassTag](d: RDD[A])(zero: B)(seqOp: (B, A) => B, combOp: (B, B) => B): B =
    d.aggregate(zero)(seqOp, combOp)

  /** Sort the dataset using a function f that evaluates each element to an orderable type */
  override def sortBy[A, B: ClassTag](d: RDD[A])(f: (A) ⇒ B)(implicit ord: math.Ordering[B]): RDD[A] =
    d.sortBy(f)

  /** Construct a traversable for the first k elements of a dataset. Will load into main mem. */
  override def take[A](d: RDD[A])(k: Int): Traversable[A] =
    d.take(k)

  override def headOption[A](d: RDD[A]): Option[A] =
    Try(d.take(1).head).toOption

  /** Load all elements of the dataset into an array in main memory. */
  override def toSeq[A](d: RDD[A]): Seq[A] =
    d.collect().toSeq

  override def flatMap[A, B: ClassTag](d: RDD[A])(f: A => TraversableOnce[B]): RDD[B] =
    d.flatMap(f)

  override def flatten[A, B: ClassTag](d: RDD[A])(implicit asRDD: A => TraversableOnce[B]): RDD[B] =
    d.flatMap(asRDD)

  override def groupBy[A, B: ClassTag](d: RDD[A])(f: A => B): RDD[(B, Iterable[A])] =
    d.groupBy(f).map { case (a, b) => (a, b) }

  /** This has type A as opposed to B >: A due to the RDD limitations */
  override def reduce[A](d: RDD[A])(op: (A, A) => A): A =
    d.reduce(op)

  override def size[A](d: RDD[A]): Long =
    d.count()

  override def isEmpty[A](d: RDD[A]): Boolean =
    d.isEmpty()

  override def zip[A, B: ClassTag](d: RDD[A])(that: RDD[B]): RDD[(A, B)] =
    d.zip(that)

  override def zipWithIndex[A](d: RDD[A]): RDD[(A, Long)] =
    d.zipWithIndex()

}