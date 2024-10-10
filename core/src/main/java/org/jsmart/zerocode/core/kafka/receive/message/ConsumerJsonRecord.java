package org.jsmart.zerocode.core.kafka.receive.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class ConsumerJsonRecord {
    private final JsonNode key;
    private final JsonNode value;
    private final Map<String, String> headers;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @JsonCreator
    public ConsumerJsonRecord(
            @JsonProperty("key") Object key,
            @JsonProperty("value") JsonNode value,
            @JsonProperty("headers") Map<String, String> headers) {
        this.key = convertToJsonNode(key);
        this.value = value;
        this.headers = headers;
    }

    private static JsonNode convertToJsonNode(Object key) {
        if (key instanceof JsonNode) {
            return (JsonNode) key;
        }
        return objectMapper.convertValue(key, JsonNode.class);
    }
    public JsonNode getKey() {
        return key;
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
                "key='" + key+ '\'' +
                ", value=" + value +
                '}';
    }
}
