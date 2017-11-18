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
    private final String stringBody;
    private final String location;

    @JsonCreator
    public Response(
                    @JsonProperty("status") int status,
                    @JsonProperty("headers") Map headers,
                    @JsonProperty("body") JsonNode body,
                    @JsonProperty("stringBody") String stringBody,
                    @JsonProperty("location") String location) {
        this.headers = headers;
        this.body = body;
        this.status = status;
        this.stringBody = stringBody;
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
    
    public String getStringBody() {
        return stringBody;
    }
    
    public String getLocation() {
        return location;
    }
    
    @Override
    public String toString() {
        return "Response{" +
               "status=" + status +
               ", headers=" + headers +
               ", body=" + body +
               ", stringBody='" + stringBody + '\'' +
               ", location='" + location + '\'' +
               '}';
    }
}
