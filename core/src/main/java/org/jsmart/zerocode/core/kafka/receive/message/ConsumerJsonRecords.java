package org.jsmart.zerocode.core.kafka.receive.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsumerJsonRecords {
    private final List<ConsumerJsonRecord> records;
    private final int size;

    @JsonCreator
    public ConsumerJsonRecords(
            @JsonProperty("records") List<ConsumerJsonRecord> records,
            @JsonProperty("size") int size) {
        this.records = records;
        this.size = size;
    }

    public ConsumerJsonRecords(List<ConsumerJsonRecord> records) {
        this(records, records != null ? records.size() : 0);
    }

    public List<ConsumerJsonRecord> getRecords() {
        return records;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "JsonRecords{" +
                "records=" + records +
                ", size=" + size +
                '}';
    }
}
