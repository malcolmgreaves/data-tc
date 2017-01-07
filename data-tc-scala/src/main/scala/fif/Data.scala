package fif

import scala.language.higherKinds
import scala.reflect.ClassTag

import simulacrum._

/**
  * Trait that abstractly represents operations that can be performed on a dataset.
  * The implementation of Data is suitable for both large-scale, distributed data
  * or in-memory structures.
  */
@typeclass
trait Data[D[_]] extends Serializable {

  /** Transform a dataset by applying f to each element. */
  def map[A, B: ClassTag](d: D[A])(f: A => B): D[B]

  def mapParition[A, B: ClassTag](d: D[A])(f: Iterable[A] => Iterable[B]): D[B]

  /** Apply a side-effecting function to each element. */
  def foreach[A](d: D[A])(f: A => Any): Unit

  def foreachPartition[A](d: D[A])(f: Iterable[A] => Any): Unit

  def filter[A](d: D[A])(f: A => Boolean): D[A]

  /**
    * Starting from a defined zero value, perform an operation seqOp on each element
    * of a dataset. Combine results of seqOp using combOp for a final value.
    */
  def aggregate[A, B: ClassTag](d: D[A])(zero: B)(seqOp: (B, A) => B,
                                                  combOp: (B, B) => B): B

  /** Sort the dataset using a function f that evaluates each element to an orderable type */
  def sortBy[A, B: ClassTag](d: D[A])(f: (A) => B)(
      implicit ord: math.Ordering[B]): D[A]

  /** Construct a traversable for the first k elements of a dataset. Will load into main mem. */
  def take[A](d: D[A])(k: Int): Traversable[A]

  def headOption[A](d: D[A]): Option[A]

  /** Load all elements of the dataset into an array in main memory. */
  def toSeq[A](d: D[A]): Seq[A]

  def flatMap[A, B: ClassTag](d: D[A])(f: A => TraversableOnce[B]): D[B]

  def flatten[A, B: ClassTag](d: D[A])(
      implicit asTraversable: A => TraversableOnce[B]): D[B]

  def groupBy[A, B: ClassTag](d: D[A])(f: A => B): D[(B, Iterable[A])]

  def reduce[A](d: D[A])(op: (A, A) => A): A

  def size[A](d: D[A]): Long

  def isEmpty[A](d: D[A]): Boolean

  def zip[A, B: ClassTag](d: D[A])(that: D[B]): D[(A, B)]

  def zipWithIndex[A](d: D[A]): D[(A, Long)]

}
