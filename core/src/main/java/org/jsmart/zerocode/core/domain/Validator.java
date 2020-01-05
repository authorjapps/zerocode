package org.jsmart.zerocode.core.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Validator {
    private final String field;
    private final JsonNode value;

    public Validator(
            @JsonProperty("field") String field,
            @JsonProperty("value") JsonNode value) {
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public JsonNode getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Validator{" +
                "field='" + field + '\'' +
                ", value=" + value +
                '}';
    }
}
