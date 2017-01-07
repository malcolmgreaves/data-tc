package com.nitro.absnlp

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.scala.DataSet

import scala.language.higherKinds
import scala.reflect.ClassTag
import scala.util.Try

import fif.Data

/**
  * Implementation of the Data typeclass with the Flink DataSet type.
  *
  * Since DataSet instances are lazy, this typeclass implementation's methods
  * have lazy (vs. eager) semantics.
  */
case object FlinkData extends Data[DataSet] {

  import Data.ops._

  override def map[A, B: ClassTag](data: DataSet[A])(f: (A) => B): DataSet[B] = {
    implicit val ti = FlinkHelper.typeInfo[B]
    data.map(f)
  }

  override def mapParition[A, B: ClassTag](d: DataSet[A])(
      f: Iterable[A] => Iterable[B]): DataSet[B] = {
    implicit val ti = FlinkHelper.typeInfo[B]
    d.mapPartition { partition =>
      f(partition.toIterable)
    }
  }

  override def foreach[A](d: DataSet[A])(f: A => Any): Unit = {
    // ignore this map operation's return type
    implicit val ti = FlinkHelper.unitTypeInformation
    d.mapPartition { partition =>
      partition.foreach(f)
      FlinkHelper.emptyUnitSeq
    }
    ()
  }

  override def foreachPartition[A](d: DataSet[A])(
      f: Iterable[A] => Any): Unit = {
    // ignore this map operation's return type
    implicit val ti = FlinkHelper.unitTypeInformation
    d.mapPartition { partition =>
      f(partition.toIterable)
      FlinkHelper.emptyUnitSeq
    }
    ()
  }

  override def filter[A](d: DataSet[A])(f: A => Boolean): DataSet[A] =
    d.filter(f)

  override def aggregate[A, B: ClassTag](data: DataSet[A])(
      zero: B)(seqOp: (B, A) => B, combOp: (B, B) => B): B = {
    implicit val ti = FlinkHelper.typeInfo[B]
    data.mapPartition { partition =>
      Seq(partition.foldLeft(zero)(seqOp))
    }.reduce(combOp).collect().reduce(combOp)
  }

  /**
    * Unimplemented!
    *
    * Flink doesn't support an API for total sorting. Must determine a correct
    * implementation using the lower-level API.
    */
  override def sortBy[A, B: ClassTag](d: DataSet[A])(f: (A) ⇒ B)(
      implicit ord: math.Ordering[B]): DataSet[A] =
    ???

  override def take[A](d: DataSet[A])(k: Int): Traversable[A] =
    d.first(k).collect()

  override def headOption[A](d: DataSet[A]): Option[A] =
    Try(d.first(1).collect().head).toOption

  override def toSeq[A](d: DataSet[A]): Seq[A] =
    d.collect()

  override def flatMap[A, B: ClassTag](d: DataSet[A])(
      f: A => TraversableOnce[B]): DataSet[B] = {
    implicit val ti = FlinkHelper.typeInfo[B]
    d.flatMap(f)
  }

  override def flatten[A, B: ClassTag](d: DataSet[A])(
      implicit asDataSet: A => TraversableOnce[B]): DataSet[B] =
    flatMap(d)(asDataSet)

  override def groupBy[A, B: ClassTag](data: DataSet[A])(
      f: A => B): DataSet[(B, Iterable[A])] = {

    val reducedToMaps = {

      implicit val ti: TypeInformation[
        scala.collection.immutable.Map[B, Iterable[A]]] =
        FlinkHelper.typeInfo(ClassTag(classOf[Map[B, Iterable[A]]]))

      data.mapPartition { partition =>
        Seq(
          partition.toIterable.groupBy(f).map {
            case (key, values) => (key, values.toIterable)
          }
        )
      }.reduce(FlinkHelper.mapCombine[B, A] _)
    }

    implicit val ti: TypeInformation[(B, Iterable[A])] =
      FlinkHelper.typeInfo(ClassTag(classOf[(B, Iterable[A])]))

    reducedToMaps.flatMap(_.toSeq)
  }

  override def reduce[A](d: DataSet[A])(op: (A, A) => A): A =
    d.reduce(op).collect().head

  override def size[A](d: DataSet[A]): Long =
    d.count()

  override def isEmpty[A](d: DataSet[A]): Boolean =
    size(d) == 0

  /**
    * Unimplemented!
    *
    * Waiting on support coming in Flink 0.10 !
    */
  override def zip[A, B: ClassTag](d: DataSet[A])(
      that: DataSet[B]): DataSet[(A, B)] =
    ???

  /**
    * Unimplemented!
    *
    * Waiting on support coming in Flink 0.10 !
    */
  override def zipWithIndex[A](d: DataSet[A]): DataSet[(A, Long)] =
    ???

}
