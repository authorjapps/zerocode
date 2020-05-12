package org.jsmart.zerocode.core.domain.builders;

import org.jsmart.zerocode.core.domain.reports.csv.ZeroCodeCsvReport;

public class ZeroCodeCsvReportBuilder {
    private String scenarioName;
    private Integer scenarioLoop;
    private String stepName;
    private Integer stepLoop;
    private String correlationId;
    private String result;
    private String method;
    private String url;
    String requestTimeStamp;
    String responseTimeStamp;
    private Double responseDelayMilliSec;
    private String host;

    public static ZeroCodeCsvReportBuilder newInstance() {
        return new ZeroCodeCsvReportBuilder();
    }

    public ZeroCodeCsvReport build() {
        ZeroCodeCsvReport built = new ZeroCodeCsvReport(scenarioName,scenarioLoop,stepName, stepLoop,
                correlationId, result, method, host, url, requestTimeStamp, responseTimeStamp, responseDelayMilliSec);
        return built;
    }

    public ZeroCodeCsvReportBuilder scenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
        return this;
    }

    public ZeroCodeCsvReportBuilder scenarioLoop(Integer scenarioLoop) {
        this.scenarioLoop = scenarioLoop;
        return this;
    }

    public ZeroCodeCsvReportBuilder stepName(String stepName) {
        this.stepName = stepName;
        return this;
    }

    public ZeroCodeCsvReportBuilder url(String url) {
        this.url = url;
        return this;
    }

    public ZeroCodeCsvReportBuilder host(String host) {
        this.host = host;
        return this;
    }

    public ZeroCodeCsvReportBuilder stepLoop(Integer stepLoop) {
        this.stepLoop = stepLoop;
        return this;
    }

    public ZeroCodeCsvReportBuilder correlationId(String correlationId) {
        this.correlationId = correlationId;
        return this;
    }

    public ZeroCodeCsvReportBuilder result(String result) {
        this.result = result;
        return this;
    }

    public ZeroCodeCsvReportBuilder method(String method) {
        this.method = method;
        return this;
    }

    public ZeroCodeCsvReportBuilder requestTimeStamp(String requestTimeStamp) {
        this.requestTimeStamp = requestTimeStamp;
        return this;
    }

    public ZeroCodeCsvReportBuilder responseTimeStamp(String responseTimeStamp) {
        this.responseTimeStamp = responseTimeStamp;
        return this;
    }

    public ZeroCodeCsvReportBuilder responseDelayMilliSec(Double responseDelayMilliSec) {
        this.responseDelayMilliSec = responseDelayMilliSec;
        return this;
    }
}
