package org.jsmart.zerocode.core.domain.builders;

import org.jsmart.zerocode.core.domain.reports.ZeroCodeReportStep;

import java.time.LocalDateTime;

public class ZeroCodeReportStepBuilder {
    Integer loop;
    String name;
    String url;
    String method;
    String correlationId;
    String operation;
    LocalDateTime requestTimeStamp;
    LocalDateTime responseTimeStamp;
    Double responseDelay;
    String result;
    String request;
    String response;
    String id;
    String assertions;
    String customLog;

    public static ZeroCodeReportStepBuilder newInstance() {
        return new ZeroCodeReportStepBuilder();
    }

    public ZeroCodeReportStep build() {
        ZeroCodeReportStep built = new ZeroCodeReportStep(
                loop, name, url, method,
                correlationId, operation, requestTimeStamp,
                responseTimeStamp, responseDelay, result,
                request, response, assertions, customLog);
        return built;
    }

    public ZeroCodeReportStepBuilder loop(Integer loop) {
        this.loop = loop;
        return this;
    }

    public ZeroCodeReportStepBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ZeroCodeReportStepBuilder url(String url) {
        this.url = url;
        return this;
    }

    public ZeroCodeReportStepBuilder method(String method) {
        this.method = method;
        return this;
    }
    public ZeroCodeReportStepBuilder correlationId(String correlationId) {
        this.correlationId = correlationId;
        return this;
    }

    public ZeroCodeReportStepBuilder operation(String operation) {
        this.operation = operation;
        return this;
    }

    public ZeroCodeReportStepBuilder requestTimeStamp(LocalDateTime requestTimeStamp) {
        this.requestTimeStamp = requestTimeStamp;
        return this;
    }

    public ZeroCodeReportStepBuilder responseTimeStamp(LocalDateTime responseTimeStamp) {
        this.responseTimeStamp = responseTimeStamp;
        return this;
    }

    public ZeroCodeReportStepBuilder responseDelay(double responseDelay) {
        this.responseDelay = responseDelay;
        return this;
    }

    public ZeroCodeReportStepBuilder request(String request) {
        this.request = request;
        return this;
    }

    public ZeroCodeReportStepBuilder response(String response) {
        this.response = response;
        return this;
    }

    public ZeroCodeReportStepBuilder result(String result) {
        this.result = result;
        return this;
    }

    public ZeroCodeReportStepBuilder assertions(String assertions) {
        this.assertions = assertions;
        return this;
    }

    public ZeroCodeReportStepBuilder id(String id) {
        this.id = id;
        return this;
    }
    public ZeroCodeReportStepBuilder customLog(String customLog) {
        this.customLog = customLog;
        return this;
    }

}
