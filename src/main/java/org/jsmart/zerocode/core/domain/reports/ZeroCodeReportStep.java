package org.jsmart.zerocode.core.domain.reports;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ZeroCodeReportStep {
    Integer loop;
    String name;
    String url;
    String correlationId;
    String operation;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime requestTimeStamp;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime responseTimeStamp;
    String responseDelay;
    String result;

    @JsonCreator
    public ZeroCodeReportStep(
            @JsonProperty("stepLoop")Integer loop,
            @JsonProperty("name")String name,
            @JsonProperty("url")String url,
            @JsonProperty("correlationId")String correlationId,
            @JsonProperty("operation")String operation,
            @JsonProperty("requestTimeStamp")LocalDateTime requestTimeStamp,
            @JsonProperty("responseTimeStamp")LocalDateTime responseTimeStamp,
            @JsonProperty("responseDelay")String responseDelay,
            @JsonProperty("result")String result) {
        this.loop = loop;
        this.name = name;
        this.url = url;
        this.correlationId = correlationId;
        this.operation = operation;
        this.requestTimeStamp = requestTimeStamp;
        this.responseTimeStamp = responseTimeStamp;
        this.responseDelay = responseDelay;
        this.result = result;
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

    public String getResponseDelay() {
        return responseDelay;
    }

    public String getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "ZeroCodeReportStep{" +
                "stepLoop=" + loop +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", correlationId='" + correlationId + '\'' +
                ", operation='" + operation + '\'' +
                ", requestTimeStamp=" + requestTimeStamp +
                ", responseTimeStamp=" + responseTimeStamp +
                ", responseDelay='" + responseDelay + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
