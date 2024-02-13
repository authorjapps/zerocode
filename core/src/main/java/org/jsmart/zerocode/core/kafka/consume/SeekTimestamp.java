package org.jsmart.zerocode.core.kafka.consume;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SeekTimestamp {

    private final String timestamp;
    private final String format;


    @JsonCreator
    public SeekTimestamp(
            @JsonProperty("timestamp") String timestamp,
            @JsonProperty("format") String format) {
        this.timestamp = timestamp;
        this.format = format;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getFormat() {
        return format;
    }

    @Override
    public String toString() {
        return "SeekTimestamp{" +
                "timestamp='" + timestamp + '\'' +
                ", format='" + format + '\'' +
                '}';
    }
}
