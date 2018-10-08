package org.jsmart.zerocode.core.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.ArrayList;
import java.util.List;

public class ConsumedRecords {
    private List<ConsumerRecord> records;

    public ConsumedRecords(List<ConsumerRecord> records) {
        this.records = records;
    }

    public List<ConsumerRecord> getRecords() {
        return records;
    }

    @Override
    public String toString() {
        return "ConsumedRecords{" +
                "records=" + records +
                '}';
    }
}
