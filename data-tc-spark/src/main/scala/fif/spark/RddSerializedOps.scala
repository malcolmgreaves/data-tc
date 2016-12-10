package fif.spark

import org.apache.spark.rdd.RDD

import scala.reflect.ClassTag

/**
 * Methods that either wrap, or operate on wrapped, values so that
 * common RDD operations are available with a natural, functional syntax.
 *
 * Let's look at Map as an example:
 *
 * {{{
 * // implemented using librray that is not extendable and doesn't implement Serialzable
 * val f: A => B = ...
 *
 * // can be anywhere, error will occur even if in local mode
 * val data: RDD[A] = ...
 *
 * // cannot do
 * data.map(f)
 * // runtime exception :(
 * // as f does not implement Serializable
 *
 * // instead do
 * Map(f)(data)
 * // will serialize it using Kryo and safely
 * // deserialize to perform map on the data RDD
 * }}}
 */
object RddSerializedOps extends Serializable {

  object Map extends Serializable {

    def apply[A, B: ClassTag](f: A => B): (RDD[A] => RDD[B]) =
      apply(KryoSerializationWrapper(f))

    def apply[A, B: ClassTag](fnSerialized: KryoSerializationWrapper[A => B]): (RDD[A] => RDD[B]) =
      (data: RDD[A]) =>
        data.mapPartitions(partition => {
          val f = fnSerialized.getValue
          partition.map(f)
        })
  }

  object FlatMap extends Serializable {

    def apply[A, B: ClassTag](f: A => TraversableOnce[B]): (RDD[A] => RDD[B]) =
      apply(KryoSerializationWrapper(f))

    def apply[A, B: ClassTag](fnSerialized: KryoSerializationWrapper[A => TraversableOnce[B]]): (RDD[A] => RDD[B]) =
      (data: RDD[A]) =>
        data.mapPartitions(partition => {
          val f = fnSerialized.getValue
          partition.flatMap(f)
        })
  }

  object Foreach extends Serializable {

    def apply[A](f: A => Any): (RDD[A] => Unit) =
      apply(KryoSerializationWrapper(f))

    def apply[A](fnSerialized: KryoSerializationWrapper[A => Any]): (RDD[A] => Unit) =
      (data: RDD[A]) =>
        data.foreachPartition(partition => {
          val f = fnSerialized.getValue
          partition.foreach(f)
        })
  }

  object Aggregate extends Serializable {

    def apply[A, B: ClassTag](zero: B, seqOp: (B, A) => B, combOp: (B, B) => B): (RDD[A] => B) =
      apply(zero, KryoSerializationWrapper(seqOp), KryoSerializationWrapper(combOp))

    def apply[A, B: ClassTag](
      zero: B,
      serSeqOp: KryoSerializationWrapper[(B, A) => B],
      serCombOp: KryoSerializationWrapper[(B, B) => B]): (RDD[A] => B) =

      (data: RDD[A]) =>
        data.aggregate(zero)(
          {
            case (b, a) =>
              val f = serSeqOp.getValue
              f(b, a)
          },
          {
            case (b1, b2) =>
              val f = serCombOp.getValue
              f(b1, b2)
          }
        )
  }

}