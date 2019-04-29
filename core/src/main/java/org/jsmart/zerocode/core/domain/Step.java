package org.jsmart.zerocode.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

@JsonInclude(JsonInclude.Include.NON_NULL)
/**
 * Do not enable this @JsonIgnoreProperties(ignoreUnknown = true) as this will suppress the test data failure,
 * let it spit out the exception(s) in case of a bad json/test input
 */
//@JsonIgnoreProperties(ignoreUnknown = true)
public class Step {
    private final Integer loop;
    private final String name;
    private final String operation;
    private final String url;
    private JsonNode request;
    private JsonNode assertions;
    private String id;
    private JsonNode stepFile;

    public Integer getLoop() {
        return loop;
    }

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

    public JsonNode getAssertions() {
        return assertions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JsonNode getStepFile() {
        return stepFile;
    }

    public void setStepFile(JsonNode stepFile) {
        this.stepFile = stepFile;
    }

    @JsonCreator
    public Step(
            @JsonProperty("stepLoop") Integer loop,
            @JsonProperty("name") String name,
            @JsonProperty("operation") String operation,
            @JsonProperty("url") String url,
            @JsonProperty("request") JsonNode request,
            @JsonProperty("assertions") JsonNode assertions
    ) {
        this.loop = loop;
        this.name = name;
        this.operation = operation;
        this.request = request;
        this.url = url;
        this.assertions = assertions;
    }

    @Override
    public String toString() {
        return "Step{" +
                "loop=" + loop +
                ", name='" + name + '\'' +
                ", operation='" + operation + '\'' +
                ", url='" + url + '\'' +
                ", request=" + request +
                ", assertions=" + assertions +
                ", id='" + id + '\'' +
                ", stepFile=" + stepFile +
                '}';
    }
}
