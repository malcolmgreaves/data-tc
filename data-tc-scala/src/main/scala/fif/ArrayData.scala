package fif

import scala.language.{ higherKinds, implicitConversions }
import scala.reflect.ClassTag

object ArrayData extends Data[Array] {

  /** Transform a dataset by applying f to each element. */
  override def map[A, B: ClassTag](data: Array[A])(f: (A) => B): Array[B] =
    data.map(f)

  override def mapParition[A, B: ClassTag](d: Array[A])(f: Iterable[A] => Iterable[B]): Array[B] =
    f(d.toIterable).toArray

  /** Apply a side-effecting function to each element. */
  override def foreach[A](d: Array[A])(f: A => Any): Unit =
    d.foreach(f)

  override def foreachPartition[A](d: Array[A])(f: Iterable[A] => Any): Unit = {
    val _ = f(d.toIterable)
  }

  override def filter[A](d: Array[A])(f: A => Boolean): Array[A] =
    d.filter(f)

  override def aggregate[A, B: ClassTag](d: Array[A])(zero: B)(seqOp: (B, A) => B, combOp: (B, B) => B): B =
    d.aggregate(zero)(seqOp, combOp)

  /** Sort the dataset using a function f that evaluates each element to an orderable type */
  override def sortBy[A, B: ClassTag](d: Array[A])(f: (A) => B)(implicit ord: math.Ordering[B]): Array[A] =
    d.sortBy(f)

  /** Construct a traversable for the first k elements of a dataset. Will load into main mem. */
  override def take[A](d: Array[A])(k: Int): Seq[A] =
    d.take(k).toSeq

  override def headOption[A](d: Array[A]): Option[A] =
    d.headOption

  /** Load all elements of the dataset into an array in main memory. */
  override def toSeq[A](d: Array[A]): Seq[A] =
    d.toSeq

  override def flatMap[A, B: ClassTag](d: Array[A])(f: A => TraversableOnce[B]): Array[B] =
    d.flatMap(f)

  override def flatten[A, B: ClassTag](d: Array[A])(implicit asTraversable: A => TraversableOnce[B]): Array[B] =
    d.flatMap(asTraversable)

  override def groupBy[A, B: ClassTag](d: Array[A])(f: A => B): Array[(B, Iterable[A])] =
    d.groupBy(f).map { case (a, b) => (a, b.toIterable) }.toArray

  /** This has type A as opposed to B >: A due to the RDD limitations */
  override def reduce[A](d: Array[A])(op: (A, A) => A): A =
    d.reduce(op)

  override def size[A](d: Array[A]): Long =
    d.length

  override def isEmpty[A](d: Array[A]): Boolean =
    d.isEmpty

  override def zip[A, B: ClassTag](d: Array[A])(that: Array[B]): Array[(A, B)] =
    d.zip(that)

  override def zipWithIndex[A](d: Array[A]): Array[(A, Long)] =
    d.zipWithIndex.map { case (a, i) => (a, i.toLong) }

}