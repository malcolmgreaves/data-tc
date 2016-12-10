package fif.spark.avroparquet

import java.io.File
import java.util.UUID

import com.holdenkarau.spark.testing.SharedSparkContext
import org.scalatest.FunSuite

class RddHelpersTest extends FunSuite with SharedSparkContext {

  import RddHelpersTest._

  test("deserialize existing avro-parquet formatted RDD") {
    RddHelpers
      .rddFromParquet[SampleEntity](sc)(preSerializedRddDocumentEntitiesPath)
      .sortBy(sortFn)
      .collect()
      .zip(entities)
      .foreach {
        case (o, l) => assert(o === l)
      }
  }

  test("serialize and deserialize same RDD, ensure contents do not change") {
    runWithTemp(deleteBefore = true) { temp =>

      val rddEntities = sc.parallelize(entities)
      RddHelpers.saveRddAsParquet[SampleEntity](sc)(temp.getAbsolutePath)(rddEntities)

      val loadedRddEntities =
        RddHelpers.rddFromParquet[SampleEntity](sc)(temp.getAbsolutePath)
          .sortBy(sortFn)

      rddEntities
        .sortBy(sortFn)
        .collect()
        .zip(loadedRddEntities.collect())
        .foreach {
          case (o, l) => assert(o === l)
        }
    }
  }

}

object RddHelpersTest {

  val preSerializedRddDocumentEntitiesPath = "data-tc-spark/src/test/resources/avroparquet_sample_entities_rdd/"

  def runWithTemp(deleteBefore: Boolean)(f: File => Unit): Unit =
    synchronized {
      val temp = File.createTempFile("sparkmod-RddHelpersTest", UUID.randomUUID().toString)
      try {
        if (deleteBefore)
          temp.delete()

        f(temp)
      } finally {
        val _ = temp.delete()
      }
    }

  val sortFn = (se: SampleEntity) => se.entityName

  val entities =
    Vector(
      SampleEntity(
        "John Smith",
        "PERSON",
        Vector(0, 1, 5, 6)
      ),
      SampleEntity(
        "John Smith",
        "PERSON",
        Vector(0, 1, 5, 6)
      ),
      SampleEntity(
        "St. Petersburg",
        "LOCATION",
        Vector(10)
      ),
      SampleEntity(
        "The Most Interesting Entity In The World",
        "MISC",
        Vector(42)
      ),
      SampleEntity(
        "baseball",
        "O",
        Vector(1, 5, 6)
      ),
      SampleEntity(
        "June 8, 2015",
        "DATE",
        Vector(0, 1, 5, 6)
      ),
      SampleEntity(
        "one million dollars",
        "MONEY",
        Vector(42)
      )
    )
      .sortBy(sortFn)

}