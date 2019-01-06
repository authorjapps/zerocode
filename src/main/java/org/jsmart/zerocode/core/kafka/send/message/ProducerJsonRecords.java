package org.jsmart.zerocode.core.kafka.send.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProducerJsonRecords {
    private final List<ProducerJsonRecord> records;
    private final Boolean async;

    @JsonCreator
    public ProducerJsonRecords(
            @JsonProperty("records") List<ProducerJsonRecord> records,
            @JsonProperty("async") Boolean async) {
        this.records = records;
        this.async = async;
    }

    public List<ProducerJsonRecord> getRecords() {
        return records;
    }

    public Boolean getAsync() {
        return async;
    }

    @Override
    public String toString() {
        return "JsonRecords{" +
                "records=" + records +
                ", async=" + async +
                '}';
    }
}
