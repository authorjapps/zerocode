package org.jsmart.zerocode.core.domain.reports;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ZeroCodeReportStep {
    private final Integer loop;
    private final String name;
    private final String url;
    private final String correlationId;
    private final String operation;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private final LocalDateTime requestTimeStamp;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private final LocalDateTime responseTimeStamp;
    private final Double responseDelay;
    private final String result;
    private final String request;
    private final String response;
    private final String assertions;

    @JsonCreator
    public ZeroCodeReportStep(
            @JsonProperty("stepLoop") Integer loop,
            @JsonProperty("name") String name,
            @JsonProperty("url") String url,
            @JsonProperty("correlationId") String correlationId,
            @JsonProperty("operation") String operation,
            @JsonProperty("requestTimeStamp") LocalDateTime requestTimeStamp,
            @JsonProperty("responseTimeStamp") LocalDateTime responseTimeStamp,
            @JsonProperty("responseDelay") Double responseDelay,
            @JsonProperty("result") String result,
            @JsonProperty("request") String request,
            @JsonProperty("response") String response,
            @JsonProperty("assertions") String assertions) {
        this.loop = loop;
        this.name = name;
        this.url = url;
        this.correlationId = correlationId;
        this.operation = operation;
        this.requestTimeStamp = requestTimeStamp;
        this.responseTimeStamp = responseTimeStamp;
        this.responseDelay = responseDelay;
        this.result = result;
        this.request = request;
        this.response = response;
        this.assertions = assertions;
    }

    public Integer getLoop() {
        return loop;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getOperation() {
        return operation;
    }

    public LocalDateTime getRequestTimeStamp() {
        return requestTimeStamp;
    }

    public LocalDateTime getResponseTimeStamp() {
        return responseTimeStamp;
    }

    public Double getResponseDelay() {
        return responseDelay;
    }

    public String getResult() {
        return result;
    }

    public String getRequest() {
        return request;
    }

    public String getResponse() {
        return response;
    }

    public String getAssertions() {
        return assertions;
    }

    @Override
    public String toString() {
        return "ZeroCodeReportStep{" +
                "loop=" + loop +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", correlationId='" + correlationId + '\'' +
                ", operation='" + operation + '\'' +
                ", requestTimeStamp=" + requestTimeStamp +
                ", responseTimeStamp=" + responseTimeStamp +
                ", responseDelay='" + responseDelay + '\'' +
                ", result='" + result + '\'' +
                ", request='" + request + '\'' +
                ", response='" + response + '\'' +
                ", assertions='" + assertions + '\'' +
                '}';
    }
}
