package fif.spark

import scala.reflect.ClassTag

/**
  * Wraps a value of an unserialized type T in a KryoSerializationWrapper[T],
  * which gives one a way to serialize T.
  *
  *
  * NOTE:
  *  The vast majority of this code is copied / based off of the classes with the same
  *  name in the Apache Shark project.
  *
  *  Original file is here (accessed on April 20, 2015):
  *  https://github.com/amplab/shark/blob/master/src/main/scala/shark/execution/serialization/KryoSerializationWrapper.scala
  *
  */
object KryoSerializationWrapper extends Serializable {

  def apply[T: ClassTag](value: T): KryoSerializationWrapper[T] =
    new KryoSerializationWrapper[T](value) {}
}

/**
  * A wrapper around some unserializable objects that make them both Java
  * serializable. Internally, Kryo is used for serialization.
  *
  * Use KryoSerializationWrapper(value) to create a wrapper.
  *
  * Note that the value contained in the wrapper is mutable. It must be
  * initialized using Java Serialization (which calls a private readObject
  * method that does the byte-by-byte deserialization).
  *
  * Also note that this class is both abstract and sealed. The only valid place
  * to create such a wrapper is the companion object's apply method.
  */
sealed abstract class KryoSerializationWrapper[T: ClassTag]
    extends Serializable {

  // the wrapped value
  // MUST BE TRANSIENT SO THAT IT IS NOT SERIALIZED
  @transient private var value: T = _

  // our serialized representation of the wrapped value.
  // MUST NOT BE TRANSIENT AS IT IS THE SERIALIZED VALUE
  private var valueSerialized: Array[Byte] = _

  /**
    * The only valid constructor. For safety, do not use the no-arg constructor.
    */
  def this(initialValue: T) = {
    this()
    this.value = initialValue
    this.valueSerialized = KryoSerializer.serialize(this.value)
  }

  def getValue: T =
    value

  /**
    * Gets the currently serialized value as a Sequence of bytes.
    *
    * If the sequence is empty, then it means that one has not called doSerializeValue().
    * Or the internal value may be null.
    */
  def getValueSerialized: Seq[Byte] =
    valueSerialized.toSeq

  // Used for Java serialization.
  private def writeObject(out: java.io.ObjectOutputStream): Unit =
    out.defaultWriteObject()

  // Used for Java Deserialization
  private def readObject(in: java.io.ObjectInputStream): Unit = {
    in.defaultReadObject()
    this.value = KryoSerializer.deserialize[T](this.valueSerialized)
  }
}
