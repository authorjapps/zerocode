package org.jsmart.zerocode.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

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
