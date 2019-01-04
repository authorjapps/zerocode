package org.jsmart.zerocode.core.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jsmart.zerocode.core.kafka.receive.message.JsonRecord;

import java.util.ArrayList;
import java.util.List;

public class ConsumedRecords {
    private final List<ConsumerRecord> records;
    private final List<JsonRecord> jsonRecords;
    private final Integer size;

    public ConsumedRecords(List<JsonRecord> jsonRecords, List<ConsumerRecord> records, Integer size) {
        this.jsonRecords = jsonRecords;
        this.records = records;
        this.size = size != null? size : records.size();
    }

    public ConsumedRecords(List<JsonRecord> jsonRecords) {
        this(jsonRecords, null, jsonRecords.size());
    }

    public ConsumedRecords(ArrayList<ConsumerRecord> rawConsumedRecords) {
        this(null, rawConsumedRecords, rawConsumedRecords.size());
    }

    public ConsumedRecords(Integer size) {
        this(null, null, size);
    }

    public List<ConsumerRecord> getRecords() {
        return records;
    }

    public Integer getSize() {
        return records != null || jsonRecords != null? size : 0;
    }

    public List<JsonRecord> getJsonRecords() {
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