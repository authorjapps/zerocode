package org.jsmart.zerocode.core.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
/*
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
    private final JsonNode request;
    private final List<Validator> validators;
    private final JsonNode sort;
    private final JsonNode assertions;
    private final String verifyMode;
    private final JsonNode verify;
    private final boolean ignoreStep;
    private String id;
    private JsonNode stepFile;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<JsonNode> stepFiles;
    private List<Object> parameterized;
    private List<String> parameterizedCsv;
    private String customLog;

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

    public List<Validator> getValidators() {
        return validators;
    }

    public JsonNode getSort() {
        return sort;
    }

    public JsonNode getAssertions() {
        return assertions;
    }

    public JsonNode getVerify() {
        return verify;
    }

    public String getVerifyMode() {
        return verifyMode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<JsonNode> getStepFiles() {
        return stepFiles;
    }

    public void setStepFiles(List<JsonNode> stepFiles) {
        this.stepFiles = stepFiles;
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

    public String getCustomLog(){ return customLog; }

    public void setCustomLog(String customLog) { this.customLog = customLog; }

    public boolean getIgnoreStep() {
        return this.ignoreStep;
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
            @JsonProperty("validators") List<Validator> validators,
            @JsonProperty("sort") JsonNode sort,
            @JsonProperty("assertions") JsonNode assertions,
            @JsonProperty("verify") JsonNode verify,
            @JsonProperty("verifyMode") String verifyMode,
            @JsonProperty("ignoreStep") boolean ignoreStep) {
        this.loop = loop;
        this.retry = retry;
        this.name = name;
        this.validators = validators;
        this.verifyMode = verifyMode;
        this.operation = operation != null? operation : method;
        this.method = method != null? method : operation;
        this.request = request;
        this.url = url;
        this.sort = sort;
        this.assertions = assertions == null || assertions.isNull() ? verify : assertions;
        this.verify = verify;
        this.ignoreStep = ignoreStep;
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
                ", validators=" + validators +
                ", assertions=" + assertions +
                ", verifyMode=" + verifyMode +
                ", verify=" + verify +
                ", id='" + id + '\'' +
                ", stepFile=" + stepFile +
                ", stepFiles=" + stepFiles +
                ", parameterized=" + parameterized +
                ", customLog=" + customLog +
                '}';
    }
}
