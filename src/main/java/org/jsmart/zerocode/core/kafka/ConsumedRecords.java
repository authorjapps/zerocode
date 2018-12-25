package org.jsmart.zerocode.core.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;

public class ConsumedRecords {
    private List<ConsumerRecord> records;
    private Integer size;

    public ConsumedRecords(List<ConsumerRecord> records) {
        this.records = records;
        this.size = records != null? records.size() : 0;
    }

    public List<ConsumerRecord> getRecords() {
        return records;
    }

    public Integer getSize() {
        return records != null? records.size() : 0;
    }

    @Override
    public String toString() {
        return "ConsumedRecords{" +
                "records=" + records +
                ", size=" + size +
                '}';
    }
}
