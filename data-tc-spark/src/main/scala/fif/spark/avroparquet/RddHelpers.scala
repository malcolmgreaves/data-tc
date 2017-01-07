package fif.spark.avroparquet

import com.nitro.scalaAvro.runtime.{
  GeneratedMessage,
  Message,
  GeneratedMessageCompanion
}
import fif.spark.RddSerializedOps
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import parquet.avro._
import parquet.hadoop.{ParquetOutputFormat, ParquetInputFormat}

import scala.language.postfixOps
import scala.reflect.ClassTag

object RddHelpers extends Serializable {

  /*
      w.r.t as{VoidTuple, Actual} :

      This is necessary to satisfy the K/V pair requirement of PairRDDFunctions implicit conversion
      to support Hadoop integration. Unlike Spark, Hadoop (used for Parquet integration here)
      always uses key/value pairs. The type has to be enforced as Tuple2[Void, PdfDocument],
      otherwise Scala's type inference will force it to be Tuple2[Null, PdfDocument],
      which would be wrong.
   */

  def asVoidTuple[A](x: A): (Void, A) =
    (null, x)

  /**
    * A unique path (either local or network) to a file or resource.
    */
  type Path = String

  def getRdd[K: ClassTag, W: ClassTag, F <: FileInputFormat[K, W]: ClassTag](
      sc: SparkContext
  )(
      p: Path
  ): RDD[(K, W)] =
    sc.newAPIHadoopFile[K, W, F](p)

  /**
    * Sets ParquetInputFormat's read support for a type of AvroReadSupport[V].
    * Evaluates to an RDD containg Vs, using Parquet + Avro for reading.
    */
  def rddFromParquet[
      V <: GeneratedMessage with Message[V]: ClassTag: GeneratedMessageCompanion](
      sc: SparkContext
  )(
      p: Path
  ): RDD[V] = {
    // protect against deprecated error...can't get around not using Job
    val job = Job.getInstance(new Configuration())

    ParquetInputFormat
      .setReadSupportClass(job, classOf[GenericAvroReadSupport[V]])

    job.getConfiguration.set(
      GenericAvroReadSupport.HAS_GENERIC_RECORD_KEY,
      implicitly[GeneratedMessageCompanion[V]].getClass.getName
    )

    // load up that RDD!
    sc.newAPIHadoopFile(
        p,
        classOf[ParquetInputFormat[V]],
        classOf[Void],
        implicitly[ClassTag[V]].runtimeClass.asInstanceOf[Class[V]],
        job.getConfiguration
      )
      // discard the uninformative Voids...only necessary for Parquet to work
      // as it's a Hadoop thing and Hadoop things need to always have
      // (key,value) pair datasets...hence why the RDD from newAPIHadoopFile
      // is of type RDD[(K,V)] in the general case
      .map { case (_, value) => value }
  }

  def saveRddAsParquet[
      V <: GeneratedMessage with Message[V]: ClassTag: GeneratedMessageCompanion](
      sc: SparkContext)(p: Path)(data: RDD[V]): Unit = {
    // protect against deprecated error...can't get around not using Job
    val job = Job.getInstance(new Configuration())

    ParquetOutputFormat.setWriteSupportClass(job, classOf[AvroWriteSupport])
    AvroParquetOutputFormat.setSchema(
      job,
      implicitly[GeneratedMessageCompanion[V]].schema
    )

    val mapF = RddSerializedOps.Map(
      implicitly[GeneratedMessageCompanion[V]].toMutable _)

    mapF(data)
      .map(asVoidTuple)
      .saveAsNewAPIHadoopFile(
        p,
        classOf[Void],
        implicitly[ClassTag[V]].runtimeClass.asInstanceOf[Class[V]],
        classOf[ParquetOutputFormat[V]],
        job.getConfiguration
      )
  }

}
