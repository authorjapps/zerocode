package org.jsmart.zerocode.core.kafka.receive.message;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;

public class ConsumerRawRecords {
    private final List<ConsumerRecord> records;
    private final int size;

    public ConsumerRawRecords(List<ConsumerRecord> records, int size) {
        this.records = records;
        this.size = size;
    }

    public ConsumerRawRecords(List<ConsumerRecord> rawConsumedRecords) {
        this(rawConsumedRecords, rawConsumedRecords.size());
    }

    public ConsumerRawRecords(Integer size) {
        this(null, size);
    }

    public List<ConsumerRecord> getRecords() {
        return records;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "ConsumedRecords{" +
                ", records=" + records +
                ", size=" + size +
                '}';
    }
}