package fif.spark

import java.nio.ByteBuffer

import org.apache.spark.serializer.{KryoSerializer => SparkKryoSerializer}
import org.apache.spark.{SparkConf, SparkEnv}

import scala.reflect.ClassTag

/**
  * JVM object serialization using Kryo. This is much more efficient, but Kryo
  * sometimes is buggy to use. We use this mainly to serialize the object
  * inspectors.
  *
  *
  * NOTE:
  *  The vast majority of this code is copied / based off of the classes with the same
  *  name in the Apache Shark project.
  *
  *  Original file is here:
  *  https://github.com/amplab/shark/blob/master/src/main/scala/shark/execution/serialization/KryoSerializationWrapper.scala
  */
object KryoSerializer extends Serializable {

  @transient private[this] lazy val s =
    new SparkKryoSerializer(
      Option(SparkEnv.get).map(_.conf).getOrElse(new SparkConf())
    )

  def serialize[T: ClassTag](o: T): Array[Byte] =
    s.newInstance().serialize(o).array()

  def deserialize[T: ClassTag](bytes: Array[Byte]): T =
    s.newInstance().deserialize[T](ByteBuffer.wrap(bytes))

}
