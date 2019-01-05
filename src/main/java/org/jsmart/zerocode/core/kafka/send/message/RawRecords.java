package org.jsmart.zerocode.core.kafka.send.message;

import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.List;

public class RawRecords {
    // -------------------------------------------------------
    // A single ProducerRecord can wrap header information too
    // for individual messages.
    // TODO- see batch for common headers per batch
    // -------------------------------------------------------
    private final List<ProducerRecord> records;
    private final Boolean async;
    private final String recordType;

    public RawRecords(List<ProducerRecord> records, Boolean async, String recordType) {
        this.records = records;
        this.async = async;
        this.recordType = recordType;
    }

    public List<ProducerRecord> getRecords() {
        return records;
    }

    public Boolean getAsync() {
        return async;
    }

    public String getRecordType() {
        return recordType;
    }

    @Override
    public String toString() {
        return "RawRecords{" +
                "records=" + records +
                ", async=" + async +
                ", recordType='" + recordType + '\'' +
                '}';
    }
}
