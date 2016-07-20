package org.jsmart.zerocode.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Map;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Response {
    private final int status;
    private final Map headers;
    private final JsonNode body;
    private final String location;

    @JsonCreator
    public Response(
            @JsonProperty("status") int status,
            @JsonProperty("headers") Map headers,
            @JsonProperty("body") JsonNode body,
            @JsonProperty("location")String location) {
        this.headers = headers;
        this.body = body;
        this.status = status;
        this.location = location;
    }

    public Map getHeaders() {
        return headers;
    }

    public JsonNode getBody() {
        return body;
    }

    public int getStatus() {
        return status;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "Response{" +
                "headers=" + headers +
                ", body=" + body +
                ", status=" + status +
                ", location='" + location + '\'' +
                '}';
    }
}
