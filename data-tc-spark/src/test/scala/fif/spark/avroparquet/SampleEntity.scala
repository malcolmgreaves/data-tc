package fif.spark.avroparquet

/**
  * Code generated from avro schemas by scalaAvro. Do not modify.
  * "ALL THESE FILES ARE YOURS—EXCEPT SAMPLEENTITY.SCALA / ATTEMPT NO MODIFICATIONS THERE"
  */
final case class SampleEntity(entityName: String,
                              entityType: String,
                              pages: Vector[Int])
    extends com.nitro.scalaAvro.runtime.GeneratedMessage
    with com.nitro.scalaAvro.runtime.Message[SampleEntity] {
  def withEntityName(__v: String): SampleEntity = copy(entityName = __v)
  def withEntityType(__v: String): SampleEntity = copy(entityType = __v)
  def withPages(__v: Vector[Int]): SampleEntity = copy(pages = __v)
  def toMutable: org.apache.avro.generic.GenericRecord = {
    val __out__ =
      new org.apache.avro.generic.GenericData.Record(SampleEntity.schema)
    __out__.put("entityName", entityName)
    __out__.put("entityType", entityType)
    __out__.put(
      "pages",
      scala.collection.JavaConversions.asJavaCollection(pages.map(_e => _e)))
    __out__
  }
  def companion = SampleEntity
}
object SampleEntity
    extends com.nitro.scalaAvro.runtime.GeneratedMessageCompanion[SampleEntity] {
  implicit def messageCompanion: com.nitro.scalaAvro.runtime.GeneratedMessageCompanion[
    SampleEntity] = this
  def schema: org.apache.avro.Schema =
    new org.apache.avro.Schema.Parser().parse(
      """{"type":"record","name":"SampleEntity","namespace":"fif.spark.avroparquet","fields":[{"name":"entityName","type":"string"},{"name":"entityType","type":"string"},{"name":"pages","type":{"type":"array","items":"int"}}]}""")
  val _arbitrary: org.scalacheck.Gen[SampleEntity] = for {
    entityName <- com.nitro.scalaAvro.runtime.AvroGenUtils.genAvroString
    entityType <- com.nitro.scalaAvro.runtime.AvroGenUtils.genAvroString
    pages <- com.nitro.scalaAvro.runtime.AvroGenUtils.genAvroArray(
      org.scalacheck.Arbitrary.arbInt.arbitrary
    )
  } yield
    SampleEntity(
      entityName = entityName,
      entityType = entityType,
      pages = pages
    )
  def fromMutable(
      generic: org.apache.avro.generic.GenericRecord): SampleEntity =
    SampleEntity(
      entityName = convertString(generic.get("entityName")),
      entityType = convertString(generic.get("entityType")),
      pages = scala.collection.JavaConversions
        .asScalaIterator(
          generic
            .get("pages")
            .asInstanceOf[org.apache.avro.generic.GenericArray[Any]]
            .iterator())
        .map(_elem => _elem.asInstanceOf[Int])
        .toVector
    )
}
