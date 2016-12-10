package fif.spark.avroparquet;

import com.nitro.scalaAvro.runtime.FromGenericRecord;
import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.apache.hadoop.conf.Configuration;
import parquet.avro.AvroParquetInputFormat;
import parquet.avro.AvroParquetReader;
import parquet.avro.AvroSchemaConverter;
import parquet.hadoop.api.ReadSupport;
import parquet.io.api.RecordMaterializer;
import parquet.schema.MessageType;

import java.io.Serializable;
import java.util.Map;

/**
 * Avro implementation of {@link ReadSupport} for Avro {@link IndexedRecord}s which cover both Avro Specific and
 * Generic. Users should use {@link AvroParquetReader} or {@link AvroParquetInputFormat} rather than using
 * this class directly.
 */
public class GenericAvroReadSupport<T> extends ReadSupport<T> implements Serializable {

    public static final String AVRO_REQUESTED_PROJECTION = "parquet.avro.projection";

    public static void setRequestedProjection(Configuration configuration, Schema requestedProjection) {
        configuration.set(AVRO_REQUESTED_PROJECTION, requestedProjection.toString());
    }

    public static final String HAS_GENERIC_RECORD_KEY = "HAS_GENERIC_RECORD_KEY";

    @Override
    public ReadSupport.ReadContext init(
            Configuration configuration,
            Map<String, String> keyValueMetaData,
            MessageType fileSchema
    ) {
        String requestedProjectionString = configuration.get(AVRO_REQUESTED_PROJECTION);
        if (requestedProjectionString != null) {
            Schema avroRequestedProjection = new Schema.Parser().parse(requestedProjectionString);
            MessageType requestedProjection = new AvroSchemaConverter().convert(avroRequestedProjection);
            fileSchema.checkContains(requestedProjection);
            return new ReadSupport.ReadContext(requestedProjection);
        } else {
            return new ReadSupport.ReadContext(fileSchema);
        }
    }

    @Override
    public RecordMaterializer<T> prepareForRead(
            Configuration configuration,
            Map<String, String> keyValueMetaData,
            MessageType fileSchema,
            ReadSupport.ReadContext readContext
    ) {
        final String schemaStr;
        if (keyValueMetaData.containsKey("parquet.avro.schema")) {
            schemaStr = keyValueMetaData.get("parquet.avro.schema");
        } else if (keyValueMetaData.containsKey("avro.schema")) {
            schemaStr = keyValueMetaData.get("avro.schema");
        } else {
            throw new RuntimeException("Expecting Avro schema under parquet.avro.schema or avro.schema");
        }
        final Schema avroSchema = new Schema.Parser().parse(schemaStr);
        final FromGenericRecord<T> implGenRec = getGenericRecordFromConf(configuration);
        return new GenericAvroMaterializer<>(readContext.getRequestedSchema(), avroSchema, implGenRec);
    }

    public static <T> FromGenericRecord<T> getGenericRecordFromConf(Configuration configuration) {
        final String cname = configuration.get(HAS_GENERIC_RECORD_KEY);
        if (cname.endsWith("$")) {
            try {
                final Class<?> c = Class.forName(cname);
                // hack to work with Scala object
                return (FromGenericRecord<T>) c.getField("MODULE$").get(c);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                // "normal" route for classes
                final Class<?> c = Class.forName(cname);
                return (FromGenericRecord<T>) c.newInstance();

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}