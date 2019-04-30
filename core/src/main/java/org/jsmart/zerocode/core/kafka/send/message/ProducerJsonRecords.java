package org.jsmart.zerocode.core.kafka.send.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProducerJsonRecords {
    private final List<ProducerJsonRecord> records;
    private final Boolean async;
    private final String file;

    @JsonCreator
    public ProducerJsonRecords(
            @JsonProperty("records") List<ProducerJsonRecord> records,
            @JsonProperty("async") Boolean async,
            @JsonProperty("file") String file) {
        this.records = records;
        this.async = async;
        this.file = file;
    }

    public List<ProducerJsonRecord> getRecords() {
        return records;
    }

    public Boolean getAsync() {
        return async;
    }

    public String getFile() {
        return file;
    }

    @Override
    public String toString() {
        return "ProducerJsonRecords{" +
                "records=" + records +
                ", async=" + async +
                ", file='" + file + '\'' +
                '}';
    }
}
