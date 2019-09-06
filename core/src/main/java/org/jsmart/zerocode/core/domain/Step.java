package org.jsmart.zerocode.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
/**
 * Do not enable this @JsonIgnoreProperties(ignoreUnknown = true) as this will suppress the test data failure,
 * let it spit out the exception(s) in case of a bad json/test input
 */
//@JsonIgnoreProperties(ignoreUnknown = true)
public class Step {
    private final Integer loop;
    private final Retry retry;
    private final String name;
    private final String method;
    private final String operation;
    private final String url;
    private JsonNode request;
    private JsonNode assertions;
    private JsonNode verifications;
    private String id;
    private JsonNode stepFile;
    private List<Object> parameterized;
    private List<String> parameterizedCsv;

    public Integer getLoop() {
        return loop;
    }

    public Retry getRetry() {
        return retry;
    }

    public String getName() {
        return name;
    }

    public String getOperation() {
        return operation;
    }

    public String getMethod() {
        return method;
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

    public JsonNode getVerifications() {
        return verifications;
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

    public List<Object> getParameterized() {
        return parameterized;
    }

    public void setParameterized(List<Object> parameterized) {
        this.parameterized = parameterized;
    }

    public List<String> getParameterizedCsv() {
        return parameterizedCsv;
    }

    public void setParameterizedCsv(List<String> parameterizedCsv) {
        this.parameterizedCsv = parameterizedCsv;
    }

    @JsonCreator
    public Step(
            @JsonProperty("stepLoop") Integer loop,
            @JsonProperty("retry") Retry retry,
            @JsonProperty("name") String name,
            @JsonProperty("operation") String operation,
            @JsonProperty("method") String method,
            @JsonProperty("url") String url,
            @JsonProperty("request") JsonNode request,
            @JsonProperty("assertions") JsonNode assertions,
            @JsonProperty("verifications") JsonNode verifications) {
        this.loop = loop;
        this.retry = retry;
        this.name = name;
        this.operation = operation != null? operation : method;
        this.method = method != null? method : operation;
        this.request = request;
        this.url = url;
        this.assertions = assertions.isNull() ? verifications : assertions;
        this.verifications = verifications;
    }

    @Override
    public String toString() {
        return "Step{" +
                "loop=" + loop +
                ", retry='" + retry + '\'' +
                ", name='" + name + '\'' +
                ", method='" + method + '\'' +
                ", operation='" + operation + '\'' +
                ", url='" + url + '\'' +
                ", request=" + request +
                ", assertions=" + assertions +
                ", verifications=" + verifications +
                ", id='" + id + '\'' +
                ", stepFile=" + stepFile +
                ", parameterized=" + parameterized +
                '}';
    }
}
