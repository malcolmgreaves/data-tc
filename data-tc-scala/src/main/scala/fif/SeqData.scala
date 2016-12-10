package fif

import scala.language.{ higherKinds, implicitConversions }
import scala.reflect.ClassTag

object SeqData extends Data[Seq] {

  /** Transform a dataset by applying f to each element. */
  override def map[A, B: ClassTag](data: Seq[A])(f: (A) => B): Seq[B] =
    data.map(f)

  override def mapParition[A, B: ClassTag](d: Seq[A])(f: Iterable[A] => Iterable[B]): Seq[B] =
    f(d.toIterable).toSeq

  /** Apply a side-effecting function to each element. */
  override def foreach[A](d: Seq[A])(f: A => Any): Unit =
    d.foreach(f)

  override def foreachPartition[A](d: Seq[A])(f: Iterable[A] => Any): Unit = {
    val _ = f(d.toIterable)
  }

  override def filter[A](d: Seq[A])(f: A => Boolean): Seq[A] =
    d.filter(f)

  override def aggregate[A, B: ClassTag](d: Seq[A])(zero: B)(seqOp: (B, A) => B, combOp: (B, B) => B): B =
    d.aggregate(zero)(seqOp, combOp)

  /** Sort the dataset using a function f that evaluates each element to an orderable type */
  override def sortBy[A, B: ClassTag](d: Seq[A])(f: (A) => B)(implicit ord: math.Ordering[B]): Seq[A] =
    d.toSeq.sortBy(f)

  /** Construct a traversable for the first k elements of a dataset. Will load into main mem. */
  override def take[A](d: Seq[A])(k: Int): Seq[A] =
    d.take(k)

  override def headOption[A](d: Seq[A]): Option[A] =
    d.headOption

  /** Load all elements of the dataset into an array in main memory. */
  override def toSeq[A](d: Seq[A]): Seq[A] =
    d.toSeq

  override def flatMap[A, B: ClassTag](d: Seq[A])(f: A => TraversableOnce[B]): Seq[B] =
    d.flatMap(f)

  override def flatten[A, B: ClassTag](d: Seq[A])(implicit asTraversable: A => TraversableOnce[B]): Seq[B] =
    d.flatten

  override def groupBy[A, B: ClassTag](d: Seq[A])(f: A => B): Seq[(B, Iterable[A])] =
    d.groupBy(f).toSeq.map { case (a, b) => (a, b.toIterable) }

  /** This has type A as opposed to B >: A due to the RDD limitations */
  override def reduce[A](d: Seq[A])(op: (A, A) => A): A =
    d.reduce(op)

  override def size[A](d: Seq[A]): Long =
    d.size

  override def isEmpty[A](d: Seq[A]): Boolean =
    d.isEmpty

  override def zip[A, B: ClassTag](d: Seq[A])(that: Seq[B]): Seq[(A, B)] =
    d.toSeq.zip(that.toSeq)

  override def zipWithIndex[A](d: Seq[A]): Seq[(A, Long)] =
    d.toSeq.zipWithIndex.map { case (a, i) => (a, i.toLong) }

}
