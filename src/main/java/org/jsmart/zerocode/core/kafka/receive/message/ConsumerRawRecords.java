package org.jsmart.zerocode.core.kafka.receive.message;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.ArrayList;
import java.util.List;

public class ConsumerRawRecords {
    private final List<ConsumerRecord> records;
    private final List<ConsumerJsonRecord> jsonRecords;
    private final Integer size;

    public ConsumerRawRecords(List<ConsumerJsonRecord> jsonRecords, List<ConsumerRecord> records, Integer size) {
        this.jsonRecords = jsonRecords;
        this.records = records;
         this.size = size;
    }

    public ConsumerRawRecords(List<ConsumerJsonRecord> jsonRecords) {
        this(jsonRecords, null, jsonRecords.size());
    }

    public ConsumerRawRecords(ArrayList<ConsumerRecord> rawConsumedRecords) {
        this(null, rawConsumedRecords, rawConsumedRecords.size());
    }

    public ConsumerRawRecords(Integer size) {
        this(null, null, size);
    }

    public List<ConsumerRecord> getRecords() {
        return records;
    }

    public Integer getSize() {
        return records != null || jsonRecords != null? size : 0;
    }

    public List<ConsumerJsonRecord> getJsonRecords() {
        return jsonRecords;
    }

    @Override
    public String toString() {
        return "ConsumedRecords{" +
                "jsonRecords=" + jsonRecords +
                ", records=" + records +
                ", size=" + size +
                '}';
    }
}