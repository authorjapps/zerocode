package org.jsmart.zerocode.core.kafka.send.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

// TODO - add timestamp, partition key etc
public class ProducerJsonRecord<K> {
    private final K key;
    private final JsonNode jsonKey;
    private final JsonNode value;
    private final Map<String, String> headers;

    @JsonCreator
    public ProducerJsonRecord(
            @JsonProperty("key") K key,
            @JsonProperty("jsonKey") JsonNode jsonKey,
            @JsonProperty("value") JsonNode value,
            @JsonProperty("headers") Map<String, String> headers) {
        this.key = key;
        this.jsonKey = jsonKey;
        this.value = value;
        this.headers = headers;
    }

    public K getKey() {
        return key;
    }

    public JsonNode getJsonKey() {
        return jsonKey;
    }

    public JsonNode getValue() {
        return value;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return "Record{" +
                "key='" + key + '\'' +
                ", jsonKey=" + jsonKey +
                ", value=" + value +
                ", headers=" + headers +
                '}';
    }
}
