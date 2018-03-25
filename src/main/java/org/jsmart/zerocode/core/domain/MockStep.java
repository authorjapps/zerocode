package org.jsmart.zerocode.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.jsmart.zerocode.core.di.ObjectMapperProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
/**
 * Do not enable this @JsonIgnoreProperties(ignoreUnknown = true) as this will suppress the test data failure,
 * let it spit out the exception(s) in case of a bad json/test input
 */
//@JsonIgnoreProperties(ignoreUnknown = true)
public class MockStep {
    private final String name;
    private final String operation;
    private final String url;
    private final JsonNode request;
    private final JsonNode response;
    private final JsonNode assertions; //<-- In case the wiremock or simulator mock throws a status code etc

    // derived value i.e. body as JSON string
    private String body;

    // derived value i.e. headers as JSON string
    private String headers;

    // derived value ie headers as Map
    private Map<String, Object> headersMap;

    public String getName() {
        return name;
    }

    public String getOperation() {
        return operation;
    }

    public String getUrl() {
        return url;
    }

    public JsonNode getRequest() {
        return request;
    }

    public JsonNode getResponse() {
        return response;
    }

    public JsonNode getAssertions() {
        return assertions;
    }

    public String getBody() {
        final JsonNode bodyNode = request.get("body");
        return bodyNode != null ? request.get("body").toString() : null;
    }

    public String getHeaders() {
        return request.get("headers").toString();
    }

    public Map<String, Object> getHeadersMap() {
        ObjectMapper objectMapper = new ObjectMapperProvider().get();
        HashMap<String, Object> headersMap = new HashMap<>();
        try {
            final JsonNode headersNode = request.get("headers");
            if (null != headersNode) {
                headersMap = (HashMap<String, Object>) objectMapper.readValue(headersNode.toString(), HashMap.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return headersMap;
    }

    @JsonCreator
    public MockStep(
            @JsonProperty("name") String name,
            @JsonProperty("operation") String operation,
            @JsonProperty("url") String url,
            @JsonProperty("request") JsonNode request,
            @JsonProperty("response") JsonNode response,
            @JsonProperty("assertions") JsonNode assertions) {
        this.name = name;
        this.operation = operation;
        this.request = request;
        this.url = url;
        this.response = response;
        this.assertions = assertions;
    }

    @Override
    public String toString() {
        return "Step{" +
                ", name='" + name + '\'' +
                ", operation='" + operation + '\'' +
                ", url='" + url + '\'' +
                ", request=" + request +
                ", response=" + response +
                ", assertions=" + assertions +
                '}';
    }
}
