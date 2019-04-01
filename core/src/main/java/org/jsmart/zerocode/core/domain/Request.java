package org.jsmart.zerocode.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Request {
    private final Map<String, Object> headers;
    private final Map<String, Object> queryParams;
    private final JsonNode body;

    @JsonCreator
    public Request(
            @JsonProperty("headers")Map<String, Object> headers,
            @JsonProperty("queryParams")Map<String, Object> queryParams,
            @JsonProperty("body")JsonNode body) {
        this.headers = headers;
        this.queryParams = queryParams;
        this.body = body;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public Map<String, Object> getQueryParams() {
        return queryParams;
    }

    public JsonNode getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Request{" +
                "headers=" + headers +
                ", queryParams=" + queryParams +
                ", body='" + body + '\'' +
                '}';
    }
}
