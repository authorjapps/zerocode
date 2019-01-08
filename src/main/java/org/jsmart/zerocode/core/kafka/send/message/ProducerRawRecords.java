package org.jsmart.zerocode.core.kafka.send.message;

import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

public class ProducerRawRecords {
    // -------------------------------------------------------
    // A single ProducerRecord can wrap header information too
    // for individual messages.
    // TODO- see batch for common headers per batch
    // -------------------------------------------------------
    private List<ProducerRecord> records;
    private final Boolean async;
    private final String recordType;
    private final String file;

    public ProducerRawRecords(List<ProducerRecord> records, Boolean async, String recordType, String file) {
        this.records = ofNullable(records).orElse(new ArrayList<>());
        this.async = async;
        this.recordType = recordType;
        this.file = file;
    }

    public List<ProducerRecord> getRecords() {
        return ofNullable(records).orElse(new ArrayList<>());
    }

    public Boolean getAsync() {
        return async;
    }

    public String getRecordType() {
        return recordType;
    }

    public String getFile() {
        return file;
    }

    public void setRecords(List<ProducerRecord> records) {
        this.records = records;
    }

    @Override
    public String toString() {
        return "ProducerRawRecords{" +
                "records=" + records +
                ", async=" + async +
                ", recordType='" + recordType + '\'' +
                ", file='" + file + '\'' +
                '}';
    }
}
