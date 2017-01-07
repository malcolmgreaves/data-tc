package fif

import scala.language.{implicitConversions, higherKinds}
import scala.reflect.ClassTag

object TravData extends Data[Traversable] {

  /** Transform a dataset by applying f to each element. */
  override def map[A, B: ClassTag](data: Traversable[A])(
      f: (A) => B): Traversable[B] =
    data.map(f)

  override def mapParition[A, B: ClassTag](d: Traversable[A])(
      f: Iterable[A] => Iterable[B]): Traversable[B] =
    f(d.toIterable).toTraversable

  /** Apply a side-effecting function to each element. */
  override def foreach[A](d: Traversable[A])(f: A => Any): Unit =
    d.foreach(f)

  override def foreachPartition[A](d: Traversable[A])(
      f: Iterable[A] => Any): Unit = {
    val _ = f(d.toIterable)
  }

  override def filter[A](d: Traversable[A])(f: A => Boolean): Traversable[A] =
    d.filter(f)

  override def aggregate[A, B: ClassTag](d: Traversable[A])(
      zero: B)(seqOp: (B, A) => B, combOp: (B, B) => B): B =
    d.aggregate(zero)(seqOp, combOp)

  /** Sort the dataset using a function f that evaluates each element to an orderable type */
  override def sortBy[A, B: ClassTag](d: Traversable[A])(f: (A) => B)(
      implicit ord: math.Ordering[B]): Traversable[A] =
    d.toSeq.sortBy(f)

  /** Construct a traversable for the first k elements of a dataset. Will load into main mem. */
  override def take[A](d: Traversable[A])(k: Int): Traversable[A] =
    d.take(k)

  override def headOption[A](d: Traversable[A]): Option[A] =
    d.headOption

  /** Load all elements of the dataset into an array in main memory. */
  override def toSeq[A](d: Traversable[A]): Seq[A] =
    d.toSeq

  override def flatMap[A, B: ClassTag](d: Traversable[A])(
      f: A => TraversableOnce[B]): Traversable[B] =
    d.flatMap(f)

  override def flatten[A, B: ClassTag](d: Traversable[A])(
      implicit asTraversable: A => TraversableOnce[B]): Traversable[B] =
    d.flatten

  override def groupBy[A, B: ClassTag](d: Traversable[A])(
      f: A => B): Traversable[(B, Iterable[A])] =
    d.groupBy(f).toTraversable.map { case (a, b) => (a, b.toIterable) }

  /** This has type A as opposed to B >: A due to the RDD limitations */
  override def reduce[A](d: Traversable[A])(op: (A, A) => A): A =
    d.reduce(op)

  override def size[A](d: Traversable[A]): Long =
    d.size

  override def isEmpty[A](d: Traversable[A]): Boolean =
    d.isEmpty

  override def zip[A, B: ClassTag](d: Traversable[A])(
      that: Traversable[B]): Traversable[(A, B)] =
    d.toSeq.zip(that.toSeq)

  override def zipWithIndex[A](d: Traversable[A]): Traversable[(A, Long)] =
    d.toSeq.zipWithIndex.map { case (a, i) => (a, i.toLong) }.toTraversable

}
