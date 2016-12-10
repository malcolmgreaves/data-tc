package fif.spark.avroparquet;

import com.nitro.scalaAvro.runtime.FromGenericRecord;
import org.apache.avro.Schema;
import parquet.io.api.GroupConverter;
import parquet.io.api.RecordMaterializer;
import parquet.schema.MessageType;

import java.io.Serializable;

class GenericAvroMaterializer<T> extends RecordMaterializer<T> implements Serializable {

    private AvroConverter<T> root;

    public GenericAvroMaterializer(
            MessageType requestedSchema,
            Schema avroSchema,
            FromGenericRecord<T> implGenRec
    ) {
        this.root = new AvroConverter<>(requestedSchema, avroSchema, implGenRec);
    }

    @Override
    public T getCurrentRecord() {
        return root.getCurrentRecord();
    }

    @Override
    public GroupConverter getRootConverter() {
        return root;
    }
}