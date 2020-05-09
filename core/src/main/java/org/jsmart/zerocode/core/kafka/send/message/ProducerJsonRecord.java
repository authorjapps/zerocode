package org.jsmart.zerocode.core.kafka.send.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.common.header.Headers;

// TODO - add timestamp, partition key etc
public class ProducerJsonRecord<K> {
    private final K key;
    private final JsonNode jsonKey;
    private final JsonNode value;
    private final Headers headers;
    private final Integer partition;

    @JsonCreator
    public ProducerJsonRecord(
            @JsonProperty("key") K key,
            @JsonProperty("jsonKey") JsonNode jsonKey,
            @JsonProperty("value") JsonNode value,
            @JsonProperty("headers") Headers headers,
            @JsonProperty("partition") Integer partition) {
        this.key = key;
        this.jsonKey = jsonKey;
        this.value = value;
        this.headers = headers;
        this.partition = partition;
    }

    public K getKey() {
        return key;
    }

    public Integer getPartition() {
        return partition;
    }

    public JsonNode getJsonKey() {
        return jsonKey;
    }

    public JsonNode getValue() {
        return value;
    }

    public Headers getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return "Record{" +
                "key='" + key + '\'' +
                ", jsonKey=" + jsonKey +
                ", value=" + value +
                ", headers=" + headers +
                ", partition=" + partition +
                '}';
    }
}
