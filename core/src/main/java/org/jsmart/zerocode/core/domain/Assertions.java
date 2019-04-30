package org.jsmart.zerocode.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Assertions {
    private final Integer status;
    private final JsonNode body;

    @JsonCreator
    public Assertions(
            @JsonProperty("status") Integer status,
            @JsonProperty("body") JsonNode body) {
        this.status = status;
        this.body = body;
    }

    public Integer getStatus() {
        return status;
    }

    public JsonNode getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Assertions{" +
                "status=" + status +
                ", body=" + body +
                '}';
    }
}
