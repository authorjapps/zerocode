package org.jsmart.zerocode.core.kafka.receive.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class ConsumerJsonRecord<K> {
    private final K key;
    private final JsonNode jsonKey;
    private final JsonNode value;

    @JsonCreator
    public ConsumerJsonRecord(
            @JsonProperty("key") K key,
            @JsonProperty("jsonKey") JsonNode jsonKey,
            @JsonProperty("value") JsonNode value) {
        this.key = key;
        this.jsonKey = jsonKey;
        this.value = value;
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

    @Override
    public String toString() {
        return "Record{" +
                "key='" + key + '\'' +
                ", jsonKey=" + jsonKey +
                ", value=" + value +
                '}';
    }
}
