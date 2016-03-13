package org.jsmart.smarttester.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
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
    private final Request request;
    private final Assertions assertions;


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

    public Request getRequest() {
        return request;
    }

    public Assertions getAssertions() {
        return assertions;
    }

    @JsonCreator
    public Step(
            @JsonProperty("loop") Integer loop,
            @JsonProperty("name") String name,
            @JsonProperty("operation") String operation,
            @JsonProperty("url") String url,
            @JsonProperty("request") Request request,
            @JsonProperty("assertions") Assertions assertions) {
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
                '}';
    }
}
