package org.jsmart.zerocode.core.kafka.send.message;

import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.List;

public class Records {
    // -------------------------------------------------------
    // A single ProducerRecord can wrap header information too
    // for individual messages.
    // TODO- see batch for common headers per batch
    // -------------------------------------------------------
    private final List<ProducerRecord> records;
    private final Boolean async;

    public Records(List<ProducerRecord> records, Boolean async) {
        this.records = records;
        this.async = async;
    }

    public List<ProducerRecord> getRecords() {
        return records;
    }

    public Boolean getAsync() {
        return async;
    }

    @Override
    public String toString() {
        return "Records{" +
                "records=" + records +
                ", async=" + async +
                '}';
    }
}
